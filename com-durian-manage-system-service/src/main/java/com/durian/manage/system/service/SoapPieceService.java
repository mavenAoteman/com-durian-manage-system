package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapPiece;

import java.util.List;

public interface SoapPieceService {

    SoapPiece queryById(Integer id);

    List<SoapPiece> queryByProductionId(Integer productionId);

    List<SoapPiece> queryByStatus(Integer status);

    SoapPiece update(SoapPiece piece);

    boolean deleteById(Integer id);
}
