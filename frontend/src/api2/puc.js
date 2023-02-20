import request from '@/utils/request2'

export function obtenerPuc() {
    return request({
        url: '/contable/gpuc',
        method: 'get'
    })
}

export function esDeMovimiento(codigo) {
    const data = {
        codigo
    }
    return request({
        url: '/contable/vcod/' + data.codigo,
        method: 'get'
    })
}
