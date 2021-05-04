import request from '@/utils/request'

export function consultar(ci, cf, fi, ff, id, ip) {
    const data = {
        ci,
        cf,
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