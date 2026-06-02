package com.durian.manage.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.SoapConsumablesDao;
import com.durian.manage.system.domain.ConsumableUnitEnum;
import com.durian.manage.system.domain.SoapConsumables;
import com.durian.manage.system.service.SoapConsumablesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 物料表(SoapConsumables)表服务实现类
 *
 * @author maven12
 * @since 2025-05-26 20:34:33
 */
@Service("soapConsumablesService")
public class SoapConsumablesServiceImpl implements SoapConsumablesService {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SoapConsumablesServiceImpl.class);

    @Resource
    private SoapConsumablesDao soapConsumablesDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SoapConsumables queryById(Integer id) {
        return this.soapConsumablesDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param soapConsumables 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<SoapConsumables> queryByPage(SoapConsumables soapConsumables, PageRequest pageRequest) {
        Long uid = currentUserIdOrThrow();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        Date purchaseTimeStart = soapConsumables.getPurchaseTime();
        Date purchaseTimeEnd = soapConsumables.getExpiryDate();

        long total = this.soapConsumablesDao.count(soapConsumables, purchaseTimeStart, purchaseTimeEnd, uid, superAdmin);
        log.info("consumables queryByPage uid={} super={} total={} {}",
                uid, superAdmin, total, JSON.toJSONString(pageRequest));
        return new PageImpl<>(
                this.soapConsumablesDao.queryAllByLimit(soapConsumables, pageRequest, uid, superAdmin),
                pageRequest, total);
    }

    @Override
    public SoapConsumables insert(SoapConsumables soapConsumables) {
        Long uid = currentUserIdOrThrow();
        soapConsumables.setOwnerUserId(uid);
        normalizeAndValidateUnit(soapConsumables);
        calcWeightInGrams(soapConsumables);
        this.soapConsumablesDao.insert(soapConsumables);
        return soapConsumables;
    }

    @Override
    public SoapConsumables update(SoapConsumables soapConsumables) {
        Long uid = currentUserIdOrThrow();
        SoapConsumables existing = this.soapConsumablesDao.queryById(soapConsumables.getId());
        if (existing == null) {
            throw ApiException.badRequest("记录不存在");
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        normalizeAndValidateUnit(soapConsumables);
        calcWeightInGrams(soapConsumables);
        this.soapConsumablesDao.update(soapConsumables);
        return this.queryById(soapConsumables.getId());
    }

    /** 自动计算克重: 油脂体积单位×0.92密度，水相体积单位×1.0，质量单位直接换算 */
    private void calcWeightInGrams(SoapConsumables c) {
        Double grams = ConsumableUnitEnum.toGrams(c.getQuantity(), c.getUnit(), c.getConsumableType());
        if (grams != null) {
            c.setWeightInGrams(Math.round(grams * 1000.0) / 1000.0);
        } else {
            c.setWeightInGrams(null);
        }
    }

    /**
     * 通过主键删除数据(逻辑删除)
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        Long uid = currentUserIdOrThrow();
        SoapConsumables existing = this.soapConsumablesDao.queryById(id);
        if (existing == null) {
            return false;
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        return this.soapConsumablesDao.deleteById(id) > 0;
    }

    @Override
    public java.util.List<SoapConsumables> queryAvailable() {
        Long uid = currentUserIdOrThrow();
        return this.soapConsumablesDao.queryAvailable(uid, CurrentUserHolder.isSuperAdmin());
    }

    private Long currentUserIdOrThrow() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        return uid;
    }

    private void assertCanModify(Long ownerUserId, Long currentUserId) {
        if (CurrentUserHolder.isSuperAdmin()) {
            return;
        }
        if (ownerUserId == null || !ownerUserId.equals(currentUserId)) {
            throw ApiException.forbidden("无权操作他人的数据");
        }
    }

    private void normalizeAndValidateUnit(SoapConsumables soapConsumables) {
        if (soapConsumables == null) {
            return;
        }
        if (soapConsumables.getUnit() == null || soapConsumables.getUnit().trim().isEmpty()) {
            soapConsumables.setUnit(ConsumableUnitEnum.G.getCode());
            return;
        }
        String unit = soapConsumables.getUnit().trim().toLowerCase();
        if (!ConsumableUnitEnum.contains(unit)) {
            throw new IllegalArgumentException("不支持的单位: " + soapConsumables.getUnit());
        }
        soapConsumables.setUnit(unit);
    }
}