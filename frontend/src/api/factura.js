import request from '@/utils/request'

export function getFactura(fact_numero) {
    const data = {
        fact_numero
    }
    return request({
        
    })
}

export function getFacturas(page_size, current_page, filter, orderby) {
    const data = {
      page_size,
      current_page,
      orderby,
      filter
    }
    return request({
      url: '/fact/fcget',
      method: 'post',
      data: data
    })
}

export function getNotasDebito(page_size, current_page, filter, orderby) {
    const data = {
        page_size,
        current_page,
        orderby,
        filter
    }
    return request({
      url: '/fact/ndget',
      method: 'post',
      data: data
    })
}

export function getNotasCredito(page_size, current_page, filter, orderby) {
    const data = {
        page_size,
        current_page,
        orderby,
        filter
    }
    return request({
      url: '/fact/ncget',
      method: 'post',
      data: data
    })
}