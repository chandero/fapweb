import request from '@/utils/request2'

export function obtenerTiposDocumento() {
    return request({
      url: '/persona/otd',
      method: 'get'
    })
}