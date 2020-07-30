import request from '@/utils/request2'

export function obtenerTiposComprobante() {
    return request({
      url: '/contable/gtpc',
      method: 'get'
    })
}

export function obtenerComprobantes() {
    return request({
        url: '/contable/all',
        method: 'get'
    })
}

export function obtenerComprobantesPage(current_page, page_size) {
    const data = {
        current_page,
        page_size
    }
    return request({
        url: '/contable/allpage/' + data.current_page + '/' + data.page_size,
        method: 'get'
    })
}

export function obtenerComprobante(tp, id) {
    const data = {
        tp,
        id
    }
    return request({
        url: '/contable/gcom/' + data.tp + '/' + data.id,
        method: 'get'
    })
}

export function obtenerAuxiliar(tp, id) {
    const data = {
        tp,
        id
    }
    return request({
        url: '/contable/gaux/' + data.tp + '/' + data.id,
        method: 'get'
    })
}