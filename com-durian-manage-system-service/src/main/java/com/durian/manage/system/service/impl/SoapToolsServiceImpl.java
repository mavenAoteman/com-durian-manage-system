package com.durian.manage.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.SoapToolsDao;
import com.durian.manage.system.domain.SoapTools;
import com.durian.manage.system.service.SoapToolsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 工具服务实现
 *
 * 权限规则：普通用户只能见/改自己的工具；超级管理员可见所有用户的工具，并可以编辑/删除任何一条。
 */
@Service("soapToolsService")
public class SoapToolsServiceImpl implements SoapToolsService {

    private static final Logger log = LoggerFactory.getLogger(SoapToolsServiceImpl.class);

    @Resource
    private SoapToolsDao soapToolsDao;

    @Override
    public SoapTools queryById(Integer id) {
        return this.soapToolsDao.queryById(id);
    }

    @Override
    public Page<SoapTools> queryByPage(SoapTools soapTools, PageRequest pageRequest) {
        Long uid = currentUserIdOrThrow();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        long total = this.soapToolsDao.count(soapTools, uid, superAdmin);
        log.info("tools queryByPage uid={} super={} total={} {}", uid, superAdmin, total, JSON.toJSONString(pageRequest));
        return new PageImpl<>(this.soapToolsDao.queryAllByLimit(soapTools, pageRequest, uid, superAdmin),
                pageRequest, total);
    }

    @Override
    public SoapTools insert(SoapTools soapTools) {
        Long uid = currentUserIdOrThrow();
        soapTools.setOwnerUserId(uid);
        this.soapToolsDao.insert(soapTools);
        return soapTools;
    }

    @Override
    public SoapTools update(SoapTools soapTools) {
        Long uid = currentUserIdOrThrow();
        SoapTools existing = this.soapToolsDao.queryById(soapTools.getId());
        if (existing == null) {
            throw ApiException.badRequest("记录不存在");
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        this.soapToolsDao.update(soapTools);
        return this.queryById(soapTools.getId());
    }

    @Override
    public boolean deleteById(Integer id) {
        Long uid = currentUserIdOrThrow();
        SoapTools existing = this.soapToolsDao.queryById(id);
        if (existing == null) {
            return false;
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        return this.soapToolsDao.deleteById(id) > 0;
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
}
