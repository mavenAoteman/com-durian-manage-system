package com.durian.manage.system.service.impl;

import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.SoapPieceDao;
import com.durian.manage.system.domain.SoapPiece;
import com.durian.manage.system.service.SoapPieceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("soapPieceService")
public class SoapPieceServiceImpl implements SoapPieceService {

    @Resource
    private SoapPieceDao soapPieceDao;

    @Override
    public SoapPiece queryById(Integer id) {
        return this.soapPieceDao.queryById(id);
    }

    @Override
    public List<SoapPiece> queryByProductionId(Integer productionId) {
        return this.soapPieceDao.queryByProductionId(productionId);
    }

    @Override
    public List<SoapPiece> queryByStatus(Integer status) {
        Long uid = currentUserIdOrThrow();
        return this.soapPieceDao.queryByStatus(status, uid, CurrentUserHolder.isSuperAdmin());
    }

    @Override
    public SoapPiece update(SoapPiece piece) {
        Long uid = currentUserIdOrThrow();
        SoapPiece existing = this.soapPieceDao.queryById(piece.getId());
        if (existing == null) {
            throw ApiException.badRequest("皂块不存在");
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        this.soapPieceDao.update(piece);
        return this.soapPieceDao.queryById(piece.getId());
    }

    @Override
    public boolean deleteById(Integer id) {
        Long uid = currentUserIdOrThrow();
        SoapPiece existing = this.soapPieceDao.queryById(id);
        if (existing == null) {
            return false;
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        return this.soapPieceDao.deleteById(id) > 0;
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
