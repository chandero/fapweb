import request from '@/utils/request2'

export function obtenerTiposComprobante() {
    return request({
        url: '/contable/gtpc',
        method: 'get'
    })
}

export function obtenerCentro() {
    return request({
        url: '/contable/gccc',
        method: 'get'
    })
}

export function obtenerTipoOperacion() {
    return request({
        url: '/contable/gcto',
        method: 'get'
    })
}

export function obtenerCodigos() {
    return request({
        url: '/contable/gpuc',
        method: 'get'
    })
}

export function obtenerCodigo(id) {
    const data = {
        id
    }
    return request({
        url: '/contable/gpbi/' + data.id,
        method: 'get'
    })
}

export function obtenerComprobantes() {
    return request({
        url: '/contable/all',
        method: 'get'
    })
}

export function obtenerComprobantesPage(current_page, page_size, order_by, filter) {
    const data = {
        current_page,
        page_size,
        order_by,
        filter
    }
    return request({
        url: '/contable/allpage',
        method: 'post',
        data: data
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

export function guardarComprobante(comprobante) {
    const data = {
        comprobante
    }
    return request({
        url: '/contable/save',
        method: 'post',
        data: data.comprobante
    })
}
