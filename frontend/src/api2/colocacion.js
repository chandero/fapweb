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

export function obtenerListaColocacion(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/colocacion/listbyid/' + data.id_colocacion,
    method: 'get'
  })
}

export function obtenerListaPorDocumento(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  }
  return request({
    url: '/colocacion/listbydocument/' + data.id_identificacion + '/' + data.id_persona,
    method: 'get'
  })  
}

export function obtenerPlan(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/colocacion/plan/' + data.id_colocacion,
    method: 'get'
  })
}