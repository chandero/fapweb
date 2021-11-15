import request from '@/utils/request'

export function obtenerGarantiaPersonal(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/gape/ogp/' + data.id_colocacion,
    method: 'get'
  })
}

export function obtenerGarantiaReal(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/gare/ogr/' + data.id_colocacion,
    method: 'get'
  })
}

export function liquidacionDePrueba(id_colocacion, cuotas, fecha_corte) {
  const data = {
    id_colocacion,
    cuotas,
    fecha_corte
  }
  return request({
    url: '/cred/ldp/' + data.id_colocacion + '/' + data.cuotas + '/' + data.fecha_corte,
    method: 'get'
  })
}
