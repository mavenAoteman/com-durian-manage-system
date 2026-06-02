# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

### Backend (Maven multi-module, Spring Boot 2.1.6, Java 8)

```bash
# Build entire project (skip tests)
mvn clean package -DskipTests

# Build with Maven profile (profiles: dev, test, prod)
mvn clean package -DskipTests -Pdev

# Run the web module
mvn spring-boot:run -pl com-durian-manage-system-web

# Compile without running (verify no errors before restart)
mvn compile -pl com-durian-manage-system-service -am
```

The Spring Boot app runs on port **8080**. The main class is `SpringbootStartApplication` in the `web` module.

### Frontend (Vue 3 + Vite + TypeScript)

```bash
cd com-durian-manage-system-web/src/main/vue

# Install dependencies
npm install

# Dev server with hot-reload (http://127.0.0.1:5173)
npm run dev

# Production build (type-check + vite build)
npm run build
```

The Vite dev server proxies API requests directly to `http://127.0.0.1:8080`.

## Product Context

This system is built for **handmade soap enthusiasts (手工皂爱好者)** to manage their own soap-making on a single platform.

**Core pain point — cost accounting (成本核算)**: tracking the cost relationship between finished soaps and their raw materials. Users today do this in Excel; the system's main value is replacing that workflow with structured data. This is not a generic CRUD admin — preserve accuracy and traceability of any change that touches material quantities, unit prices, batch costs, or consumable usage. Note the existing snapshot pattern: `SoapConsumablesUsage` records `unitPrice` at usage time so future price changes don't rewrite history.

**Project shape — frontend and backend are NOT separated (前后端不分离)**: the Vue 3 source lives inside the Maven web module at `com-durian-manage-system-web/src/main/vue/` and ships together with Spring Boot. Do not introduce a standalone frontend project, a separate deploy pipeline, or a split repo — keep everything in this one Maven multi-module project.

## Architecture

A monolithic Spring Boot + Vue 3 web app for tracking soap-making materials (oils, consumables, tools) and production batches.

### Module Dependency Chain

```
web (Spring Boot + Vue frontend)
 └── service (business logic)
      └── dao (MyBatis data access)
           ├── domain (entity POJOs + enums)
           └── common (shared utilities, Druid/MyBatis dependencies)
```

All modules share package base `com.durian.manage.system.{layer}`.

### Backend Layers

- **Controllers** (`web` module): `@RestController` with `ResponseEntity<>` return types. Paths: `/soapConsumables`, `/comDurianOil`, `/soapTools`, `/soapProduction`, `/soapConsumablesUsage`
- **Services** (`service` module): Interface + `Impl` pattern, `@Service` with `@Resource` DAO injection. Pagination via Spring Data `Page`/`PageRequest`.
- **DAO** (`dao` module): MyBatis interfaces with XML mapper files in `src/main/resources/mapper/`. `@MapperScan` configured in `DiagDataSourceConfig`.
- **Domain** (`domain` module): POJOs implementing `Serializable`, plus enum classes like `ConsumableTypeEnum` and `ConsumableUnitEnum`.

### Database

- MySQL 5.7 (`durian` database) via Alibaba Druid connection pool
- Custom `DiagDataSourceConfig` (excludes Spring Boot's `DataSourceAutoConfiguration`)
- Schema at `com-durian-manage-system-dao/src/main/resources/sql/all.sql`
- `soapProduction.totalPiecePrice` is a **SQL computed column** — `coalesce((select sum(price) from soap_piece where production_id = p.id), 0)` — not stored in Java, populated by the MyBatis mapper's `queryAllByLimit` and `queryAll` SQL.
- **Status field values differ by entity** — always check the domain class or SQL comments, not assumptions:
  - `SoapConsumables.status`: 0=deleted, 1=active, 2=used, 3=expired (logical deletion)
  - `SoapPiece.status`: 1=在售, 2=已售, 3=赠送, 4=打包中
  - `SoapProduction.status`: 1=生产中, 2=晾皂中, 3=成熟中, 4=已切块, 5=已成熟
  - `SoapPackage.status`: 1=待内包装, 2=待外包装, 3=已完成

### Key Backend Patterns

- **Enums drive UI dropdowns**: `ConsumableTypeEnum` and `ConsumableUnitEnum` have a `getDescriptions()` method that returns label-value pairs. Controllers expose these via dedicated endpoints (e.g., `/consumableTypes`, `/consumableUnits`).
- **Pagination**: All list endpoints use `PageRequest.of(pageIndex-1, pageSize)` with Spring Data's `Page` return type.
- **RequestFilter**: Sets CORS headers (`Access-Control-Allow-Origin: *`) and adds a `logid` to MDC for request tracing.
- **Unit validation**: `SoapConsumablesServiceImpl` normalizes and validates consumable units against `ConsumableUnitEnum`.

### Soap Production Workflow

The production system follows a real soap maker's workflow with state transitions:

```
生产中(1) ──[晾皂]──> 成熟期(2) ──[成熟]──> 已成熟(3) ──[切块]──> 已切块入库(4)
```

**Creating a batch** (`POST /soapProduction/add`):
1. Frontend wizard (`soap-production-create.vue`) collects name, dates, weight, and a list of consumable usages
2. Backend calculates `totalCost = sum(quantityUsed × unitPrice)`, validates stock, generates batch number
3. Saves `SoapProduction`, `SoapConsumablesUsage` records, and deducts `SoapConsumables.quantity` — all in one `@Transactional`
4. Inventory is automatically marked `status=2` (已用完) when quantity drops to 0

**Tables involved**:
- `soap_production` — batch info with status, weights, dates, total cost
- `soap_consumables_usage` — links a production to consumables with quantity/price snapshot
- `soap_piece` — individual cut pieces after maturation, with weight, price, and sale status
- `soap_consumables` — `quantity` is `decimal(10,3)` for precise measurements

**Key endpoints**:
| Endpoint | Purpose |
|----------|---------|
| `GET /soapProduction/consumables` | Available consumables for the create wizard |
| `POST /soapProduction/add` | Create batch (with `usages[]` in body) |
| `GET /soapProduction/{id}` | Detail with usages + pieces populated |
| `PUT /soapProduction/{id}/status` | State transition |
| `POST /soapProduction/{id}/pieces` | Cut batch into pieces |

### Packaging Workflow (`soap_package`)

Three-tier packaging: inner materials → outer materials → complete. Piece status moves `1=在售 → 4=打包中 → 2=已售`.

**Tables**:
- `soap_package` — batch number, status (1=待内包装/2=待外包装/3=已完成), `totalMaterialCost`, `created`
- `soap_package_piece` — links package to pieces
- `soap_package_material` — consumable usage per package (`materialType`: inner/outer/gift)

**Key endpoints**:
| Endpoint | Purpose |
|----------|---------|
| `GET /soapPackage/availablePieces` | Pieces that are matured (prod status=5) + for-sale (piece status=1) |
| `POST /soapPackage/create` | Create package with pieceIds + all materials |
| `GET /soapPackage/dashboardStats` | Total cost/revenue/profit from `soapProduction` (matured batches) |
| `PUT /soapPackage/{id}` | Full update: pieces + materials (diff mode) |

**Material cost is tracked at package level**, not production level — packaging materials (boxes, tape, etc.) are separate from soap-making ingredients.

### Dashboard / 监控大屏

`dashboard.vue` is the homepage. It calls `getDashboardStats()` which aggregates:
- **总成本**: sum of `soapProduction.totalCost` for all matured (status=5) batches
- **总售价**: sum of `soapProduction.totalPiecePrice` (皂块售价) for all matured batches
- **预计收益**: 总售价 - 总成本
- Production pipeline counts, inventory alerts, 30-day packaging trend chart

### Frontend (Vue 3)

Built on the `vue-manage-system` admin template (lin-xin). Uses Composition API (`<script setup lang="ts">`) with Element Plus component library.

Key source locations:
- `src/api/index.ts` — all backend API calls, base URL `http://127.0.0.1:8080`
- `src/router/index.ts` — hash-based routing with auth guard
- `src/utils/request.ts` — Axios instance with interceptors
- `src/views/` — page components: `tools.vue`, `oils.vue`, `consumables.vue`, `soap-production.vue`
- `src/components/` — reusable edit/detail dialogs for each entity type
