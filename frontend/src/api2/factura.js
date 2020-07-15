import request from '@/utils/request2'

export function obtenerFacturas() {
    return request({
      url: '/factura/all',
      method: 'get'
    })
}
  

export function obtenerFactura(id) {
    const data = {
      id
    }
    return request({
      url: '/factura/get/' + data.id,
      method: 'get'
    })
}
  
export function obtenerFacturaPorRangoNumero(start, end) {
    const data = {
      start,
      end
    }
    return request({
      url: '/factura/gbnr/' + data.start + '/' + data.end,
      method: 'get'
    })
}

export function obtenerFacturaPorRangoFecha(start, end) {
    const data = {
      start,
      end
    }
    return request({
      url: '/factura/gbdr/' + data.start + '/' + data.end,
      method: 'get'
    })
}

export function obtenerItems(id) {
    const data = {
        id
    }
    return request({
        url: '/factura/gitm/' + data.id,
        method: 'get'
    })
}