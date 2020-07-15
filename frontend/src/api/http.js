import request from '@/utils/request'

export function enviarFactura(id) {
    const data = {
        id
    }
    return request({
      url: '/http/st/' + data.id,
      method: 'get'
    })
  }