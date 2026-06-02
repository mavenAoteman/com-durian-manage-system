package com.durian.manage.system.web.controller;

import com.durian.manage.system.domain.SoapPiece;
import com.durian.manage.system.service.SoapPieceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 皂块(SoapPiece)表控制层
 */
@RestController
@RequestMapping("soapPiece")
public class SoapPieceController {

    @Resource
    private SoapPieceService soapPieceService;

    @GetMapping("list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "100") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<SoapPiece> all = soapPieceService.queryByStatus(status);
        int total = all.size();
        int from = (pageIndex - 1) * pageSize;
        List<SoapPiece> page = from >= total ? new java.util.ArrayList<SoapPiece>() : all.subList(from, Math.min(from + pageSize, total));
        result.put("code", 200);
        result.put("data", page);
        result.put("totalElements", total);
        result.put("message", "获取成功");
        return ResponseEntity.ok(result);
    }
}