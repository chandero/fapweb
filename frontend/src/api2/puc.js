import request from '@/utils/request2'

export function obtenerPuc() {
    return request({
        url: '/contable/gpuc',
        method: 'get'
    })
}
