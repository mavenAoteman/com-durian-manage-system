-- 将物料单位从自由文本改为枚举可选值
-- 执行前建议先备份表

-- 历史数据兼容：将旧值 g/ml 归一到 g
UPDATE soap_consumables
SET unit = 'g'
WHERE unit = 'g/ml' OR unit IS NULL OR TRIM(unit) = '';

-- 如有其他非枚举值，也统一兜底到 g
UPDATE soap_consumables
SET unit = 'g'
WHERE unit NOT IN ('g','ml','mg','kg','ug','ng','l','ul','cl','dl','tsp','tbsp','cup','drop','oz','lb');

ALTER TABLE soap_consumables
MODIFY COLUMN unit ENUM('g','ml','mg','kg','ug','ng','l','ul','cl','dl','tsp','tbsp','cup','drop','oz','lb')
NOT NULL DEFAULT 'g' COMMENT '单位类型';
