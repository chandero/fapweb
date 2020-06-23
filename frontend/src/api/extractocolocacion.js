import request from '@/utils/request'

export function obtenerExtractoColocacion(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/exco/oec/' + data.id_colocacion,
    method: 'get'
  })
}

export function obtenerExtractoColocacionDetallado(id_colocacion, id_cbte_colocacion, fecha_extracto) {
  const data = {
    id_colocacion,
    id_cbte_colocacion,
    fecha_extracto
  }
  return request({
    url: '/exco/oed/' + data.id_colocacion + '/' + data.id_cbte_colocacion + '/' + data.fecha_extracto,
    method: 'get'
  })
}
