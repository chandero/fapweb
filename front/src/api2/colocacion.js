import request from '@/utils/request2'

export function obtenerColocacion(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/colocacion/get/' + data.id_colocacion,
    method: 'get'
  })
}
