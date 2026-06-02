package com.durian.manage.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.ComDurianOilDao;
import com.durian.manage.system.domain.ComDurianOil;
import com.durian.manage.system.service.ComDurianOilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 油脂（配方灵感）权限规则：
 *  - 普通用户：看到「归属于超级管理员的公共油脂」+「自己创建的油脂」；
 *             只能增删改自己创建的，不能动超管的公共数据
 *  - 超级管理员：看到所有油脂；可以增删改任何一条
 */
@Service("comDurianOilService")
public class ComDurianOilServiceImpl implements ComDurianOilService {

    private static final Logger log = LoggerFactory.getLogger(ComDurianOilServiceImpl.class);

    @Resource
    private ComDurianOilDao comDurianOilDao;

    @Override
    public ComDurianOil queryById(Integer id) {
        return this.comDurianOilDao.queryById(id);
    }

    @Override
    public Page<ComDurianOil> queryByPage(ComDurianOil comDurianOil, PageRequest pageRequest) {
        Long uid = currentUserIdOrThrow();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        long total = this.comDurianOilDao.count(comDurianOil, uid, superAdmin);
        log.info("oil queryByPage uid={} super={} total={} offset={} size={}",
                uid, superAdmin, total, pageRequest.getOffset(), pageRequest.getPageSize());
        List<ComDurianOil> list = this.comDurianOilDao.queryAllByLimit(comDurianOil, pageRequest, uid, superAdmin);
        log.debug("oil queryByPage list={}", JSON.toJSONString(list));
        return new PageImpl<>(list, pageRequest, total);
    }

    @Override
    public ComDurianOil insert(ComDurianOil comDurianOil) {
        Long uid = currentUserIdOrThrow();
        // 普通用户创建的记录归属自己；超管创建的默认作为"个人"记录（不主动建系统默认，避免误操作）
        comDurianOil.setOwnerUserId(uid);
        if (comDurianOil.getCreateTime() == null) {
            comDurianOil.setCreateTime(new Date());
        }
        comDurianOil.setModifyTime(new Date());
        if (comDurianOil.getStatus() == null) {
            comDurianOil.setStatus("1");
        }
        this.comDurianOilDao.insert(comDurianOil);
        return comDurianOil;
    }

    @Override
    public ComDurianOil update(ComDurianOil comDurianOil) {
        Long uid = currentUserIdOrThrow();
        ComDurianOil existing = this.comDurianOilDao.queryById(comDurianOil.getId());
        if (existing == null) {
            throw ApiException.badRequest("记录不存在");
        }
        assertCanModify(existing, uid);
        this.comDurianOilDao.update(comDurianOil);
        return this.queryById(comDurianOil.getId());
    }

    @Override
    public boolean deleteById(Integer id) {
        Long uid = currentUserIdOrThrow();
        ComDurianOil existing = this.comDurianOilDao.queryById(id);
        if (existing == null) {
            return false;
        }
        assertCanModify(existing, uid);
        return this.comDurianOilDao.deleteById(id) > 0;
    }

    @Override
    public List<ComDurianOil> queryAll() {
        Long uid = currentUserIdOrThrow();
        return this.comDurianOilDao.queryAll(uid, CurrentUserHolder.isSuperAdmin());
    }

    private Long currentUserIdOrThrow() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        return uid;
    }

    private void assertCanModify(ComDurianOil oil, Long currentUserId) {
        if (CurrentUserHolder.isSuperAdmin()) {
            return;
        }
        Long owner = oil.getOwnerUserId();
        if (owner == null || !owner.equals(currentUserId)) {
            throw ApiException.forbidden("公共油脂仅超级管理员可修改");
        }
    }
}
