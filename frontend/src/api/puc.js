import request from '@/utils/request'

export function obtenerPuc() {
  return request({
    url: '/puc/ol',
    method: 'get'
  })
}

export function obtenerNombrePuc(codigo) {
  const data = {
    codigo
  }
  return request({
    url: '/puc/ocn/' + data.codigo,
    method: 'get'
  })
}
