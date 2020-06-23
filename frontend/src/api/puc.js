import request from '@/utils/request'

export function obtenerNombrePuc(codigo) {
  const data = {
    codigo
  }
  return request({
    url: '/puc/ocn/' + data.codigo,
    method: 'get'
  })
}
