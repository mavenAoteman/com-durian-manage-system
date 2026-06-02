package com.durian.manage.system.service.impl;

import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.SoapConsumablesUsageDao;
import com.durian.manage.system.domain.SoapConsumablesUsage;
import com.durian.manage.system.service.SoapConsumablesUsageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 物料使用明细(SoapConsumablesUsage)表服务实现类
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
@Service("soapConsumablesUsageService")
public class SoapConsumablesUsageServiceImpl implements SoapConsumablesUsageService {
    @Resource
    private SoapConsumablesUsageDao soapConsumablesUsageDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SoapConsumablesUsage queryById(Integer id) {
        return this.soapConsumablesUsageDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param soapConsumablesUsage 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<SoapConsumablesUsage> queryByPage(SoapConsumablesUsage soapConsumablesUsage, PageRequest pageRequest) {
        long total = this.soapConsumablesUsageDao.count(soapConsumablesUsage);
        return new PageImpl<>(this.soapConsumablesUsageDao.queryAllByLimit(soapConsumablesUsage, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param soapConsumablesUsage 实例对象
     * @return 实例对象
     */
    @Override
    public SoapConsumablesUsage insert(SoapConsumablesUsage soapConsumablesUsage) {
        this.soapConsumablesUsageDao.insert(soapConsumablesUsage);
        return soapConsumablesUsage;
    }

    /**
     * 修改数据
     *
     * @param soapConsumablesUsage 实例对象
     * @return 实例对象
     */
    @Override
    public SoapConsumablesUsage update(SoapConsumablesUsage soapConsumablesUsage) {
        this.soapConsumablesUsageDao.update(soapConsumablesUsage);
        return this.queryById(soapConsumablesUsage.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.soapConsumablesUsageDao.deleteById(id) > 0;
    }

    @Override
    public Page<SoapConsumablesUsage> queryWithDetail(PageRequest pageRequest) {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        long total = this.soapConsumablesUsageDao.countWithDetail(uid, superAdmin);
        List<SoapConsumablesUsage> list = this.soapConsumablesUsageDao.queryAllWithDetail(pageRequest, uid, superAdmin);
        for (SoapConsumablesUsage u : list) {
            double q = u.getQuantityUsed() != null ? u.getQuantityUsed() : 0;
            double p = u.getUnitPrice() != null ? u.getUnitPrice() : 0;
            u.setSubtotal(Math.round(q * p * 100.0) / 100.0);
        }
        return new PageImpl<>(list, pageRequest, total);
    }
}
