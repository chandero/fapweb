import request from '@/utils/request'

export function consultar(ci, cf, anho, fi, ff, id, ip) {
    const data = {
        ci,
        cf,
        anho,
        fi,
        ff,
        id,
        ip
    }
    return request({
        url: '/auxi/cons',
        method: 'post',
        data: data
    })
}

export function aExcel(ci, cf, anho, fi, ff, id, ip) {
    const data = {
        ci,
        cf,
        anho,
        fi,
        ff,
        id,
        ip
    }
    return request({
        url: '/auxi/axls',
        method: 'post',
        responseType: 'arraybuffer',
        data: data
    })
}