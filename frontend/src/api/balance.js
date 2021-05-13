import request from '@/utils/request'

export function consultar(ci, cf, fc, n) {
    const data = {
        ci,
        cf,
        fc,
        n
}
    return request({
        url: '/bala/cons',
        method: 'post',
        data: data
    })
}

export function aExcel(ci, cf, fc, n, cm) {
    const data = {
        ci,
        cf,
        fc,
        n,
        cm
    }
    return request({
        url: '/bala/axls',
        method: 'post',
        responseType: 'arraybuffer',
        data: data
    })
}