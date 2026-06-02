import request from '../utils/request';

const BASE_URL = 'http://127.0.0.1:8080';

// ============ 用户认证 ============

export const authRegister = (data: { username: string; password: string; email?: string; captchaPassed: boolean }) => {
    return request({
        url: BASE_URL + '/auth/register',
        method: 'post',
        data: JSON.stringify(data),
    });
};

export const authLogin = (data: { username: string; password: string; captchaPassed?: boolean }) => {
    return request({
        url: BASE_URL + '/auth/login',
        method: 'post',
        data: JSON.stringify(data),
    });
};

export const authPrecheck = (username: string) => {
    return request({
        url: BASE_URL + '/auth/precheck?username=' + encodeURIComponent(username),
        method: 'get',
    });
};

export const authMe = () => {
    return request({
        url: BASE_URL + '/auth/me',
        method: 'get',
    });
};

export const authUpdateProfile = (data: { workshopName: string; bio: string }) => {
    return request({
        url: BASE_URL + '/auth/me/profile',
        method: 'put',
        data: JSON.stringify(data),
    });
};

export const authChangePassword = (data: { oldPassword: string; newPassword: string }) => {
    return request({
        url: BASE_URL + '/auth/me/password',
        method: 'put',
        data: JSON.stringify(data),
    });
};

export const fetchData = () => {
    return request({
        url: 'https://www.fastmock.site/mock/dc695d037038802def4b989ba4650c3f/vms/getUser',
        method: 'post'
    });
};

export const addSoapTools = async (soapToolsData) => {
    return request({
        url: BASE_URL + '/soapTools/add',
        method: 'post',
        data: JSON.stringify(soapToolsData)
    });
};

export const queryByPageSoapTools = async (param) => {
    console.log(param)
    return request({
        url: BASE_URL + '/soapTools/queryByPage?pageIndex='+param.pageIndex+'&pageSize='+param.pageSize+'&name='+param.name,
        method: 'get'
    });
};

export const deleteSoapTools = async (index) => {
    console.log(index);
    return request({
        url: BASE_URL + '/soapTools/delete?id='+index,
        method: 'delete'
    });
};

export const addOil = async (soapToolsData) => {
    return request({
        url: BASE_URL + '/comDurianOil/add',
        method: 'post',
        data: JSON.stringify(soapToolsData)
    });
};

export const queryByPageOils = async (param) => {
    console.log(param)
    return request({
        url: BASE_URL + '/comDurianOil/queryByPage?pageIndex='+param.pageIndex+'&pageSize='+param.pageSize+'&name='+param.name,
        method: 'get'
    });
};

export const deleteOil = async (index) => {
    console.log(index);
    return request({
        url: BASE_URL + '/comDurianOil/delete?id='+index,
        method: 'delete'
    });
};

export const queryAllOils = async () => {
    return request({
        url: BASE_URL + '/comDurianOil/all',
        method: 'get'
    });
};


export const addSoapConsumables = async (soapToolsData) => {
    console.log(soapToolsData)
    return request({
        url: BASE_URL + '/soapConsumables/add',
        method: 'post',
        data: JSON.stringify(soapToolsData)
    });
};

export const queryByPageSoapConsumables = async (param) => {
    console.log(param)
    let url = BASE_URL + '/soapConsumables/queryByPage?pageIndex='+param.pageIndex+'&pageSize='+param.pageSize+'&name='+param.name;
    
    // 添加额外的查询参数
    if (param.brand) {
      url += '&brand=' + param.brand;
    }
    if (param.consumableType !== null && param.consumableType !== undefined) {
      url += '&consumableType=' + param.consumableType;
    }
    if (param.purchaseChannel) {
      url += '&purchaseChannel=' + param.purchaseChannel;
    }
    if (param.startDate) {
      url += '&startDate=' + param.startDate;
    }
    if (param.endDate) {
      url += '&endDate=' + param.endDate;
    }
    if (param.status !== undefined && param.status !== null) {
      url += '&status=' + param.status;
    }
    
    return request({
        url: url,
        method: 'get'
    });
};

export const getConsumableTypes = async () => {
    return request({
        url: BASE_URL + '/soapConsumables/consumableTypes',
        method: 'get'
    });
};

export const getConsumableUnits = async () => {
    return request({
        url: BASE_URL + '/soapConsumables/consumableUnits',
        method: 'get'
    });
};

export const deleteSoapConsumables = async (index) => {
    console.log(index);
    return request({
        url: BASE_URL + '/soapConsumables/delete?id='+index,
        method: 'delete'
    });
};

// ===== 物料消耗记录 API =====

export const queryConsumablesUsage = async (param) => {
    return request({
        url: BASE_URL + '/soapConsumablesUsage/queryByPage?pageIndex=' + param.pageIndex + '&pageSize=' + param.pageSize,
        method: 'get'
    });
};


// ===== 手工皂生产相关 API =====

export const getAvailableConsumables = async () => {
    return request({
        url: BASE_URL + '/soapProduction/consumables',
        method: 'get'
    });
};

export const addSoapProduction = async (data) => {
    return request({
        url: BASE_URL + '/soapProduction/add',
        method: 'post',
        data: JSON.stringify(data)
    });
};

export const queryByPageSoapProduction = async (param) => {
    let url = BASE_URL + '/soapProduction/queryByPage?pageIndex='+param.pageIndex+'&pageSize='+param.pageSize;
    if (param.name) url += '&name=' + param.name;
    if (param.status !== null && param.status !== undefined) url += '&status=' + param.status;
    return request({ url, method: 'get' });
};

export const getSoapProductionDetail = async (id) => {
    return request({
        url: BASE_URL + '/soapProduction/' + id,
        method: 'get'
    });
};

export const updateSoapProductionStatus = async (id, status, actualWeight?) => {
    return request({
        url: BASE_URL + '/soapProduction/' + id + '/status',
        method: 'put',
        data: JSON.stringify({ status, actualWeight })
    });
};

export const getSoapPieces = async (productionId) => {
    return request({
        url: BASE_URL + '/soapProduction/' + productionId + '/pieces',
        method: 'get'
    });
};

export const addSoapPieces = async (productionId, pieces) => {
    return request({
        url: BASE_URL + '/soapProduction/' + productionId + '/pieces',
        method: 'post',
        data: JSON.stringify(pieces)
    });
};

export const updateSoapPiecesBatch = async (productionId, pieces) => {
    return request({
        url: BASE_URL + '/soapProduction/' + productionId + '/pieces/batch',
        method: 'put',
        data: JSON.stringify(pieces)
    });
};

export const updateSoapPiece = async (pieceId, data) => {
    return request({
        url: BASE_URL + '/soapProduction/pieces/' + pieceId,
        method: 'put',
        data: JSON.stringify(data)
    });
};

export const deleteSoapPiece = async (pieceId) => {
    return request({
        url: BASE_URL + '/soapProduction/pieces/' + pieceId,
        method: 'delete'
    });
};

export const deleteSoapProduction = async (id) => {
    return request({
        url: BASE_URL + '/soapProduction/delete?id=' + id,
        method: 'delete'
    });
};
// ===== 皂块 API =====
export const queryByPageSoapPiece = async (param) => {
    let url = BASE_URL + '/soapPiece/list?pageIndex=' + param.pageIndex + '&pageSize=' + param.pageSize;
    if (param.status !== null && param.status !== undefined) url += '&status=' + param.status;
    return request({ url, method: 'get' });
};

// ===== 打包发货 API =====
export const getAvailablePieces = async () => {
    return request({ url: BASE_URL + '/soapPackage/availablePieces', method: 'get' });
};
export const getDashboardStats = async () => {
    return request({ url: BASE_URL + '/soapPackage/dashboardStats', method: 'get' });
};
export const getDashboardStatsByRange = async (startDate?: string, endDate?: string) => {
    let url = BASE_URL + '/soapPackage/dashboardStatsByRange';
    if (startDate) url += '?startDate=' + startDate;
    if (endDate) url += (startDate ? '&' : '?') + 'endDate=' + endDate;
    return request({ url, method: 'get' });
};
export const queryByPageSoapPackage = async (param) => {
    let url = BASE_URL + '/soapPackage/queryByPage?pageIndex=' + param.pageIndex + '&pageSize=' + param.pageSize;
    if (param.status !== null && param.status !== undefined) url += '&status=' + param.status;
    if (param.startDate) url += '&startDate=' + param.startDate;
    if (param.endDate) url += '&endDate=' + param.endDate;
    return request({ url, method: 'get' });
};
export const getPackageDetail = async (id) => {
    return request({ url: BASE_URL + '/soapPackage/' + id, method: 'get' });
};
export const createPackage = async (data) => {
    return request({ url: BASE_URL + '/soapPackage/create', method: 'post', data: JSON.stringify(data) });
};
export const updatePackageStatus = async (id, status) => {
    return request({ url: BASE_URL + '/soapPackage/' + id + '/status', method: 'put', data: JSON.stringify({ status }) });
};
export const deletePackage = async (id) => {
    return request({ url: BASE_URL + '/soapPackage/' + id, method: 'delete' });
};
export const removePiecesFromPackage = async (id, pieceIds) => {
    return request({ url: BASE_URL + '/soapPackage/' + id + '/removePieces', method: 'post', data: JSON.stringify({ pieceIds }) });
};
export const updatePackage = async (id, data) => {
    return request({ url: BASE_URL + '/soapPackage/' + id, method: 'put', data: JSON.stringify(data) });
};
