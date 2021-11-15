import request from '@/utils/request'

export function obtenerTodos() {
  return request({
    url: '/pegr/get',
    method: 'get'
  })
}

export function buscarPeriodoGraciaPorDocumento(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  }
  return ({
    url: '/pegr/bpd/' + data.id_identificacion + '/' + data.id_persona,
    method: 'get'
  })
}

export function buscarPeriodoGraciaPorColocacion(id_colocacion) {
  const data = {
    id_colocacion
  }
  return ({
    url: '/pegr/bpc/' + data.id_colocacion,
    method: 'get'
  })
}

export function normalizarColocacionPeriodoGracia(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/pegr/norm/' + data.id_colocacion,
    method: 'get'
  })
}

export function normalizarreversoColocacionPeriodoGracia(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/pegr/norv/' + data.id_colocacion,
    method: 'get'
  })
}

export function actualizarDiasPeriodoGracia(id_colocacion, dias) {
  const data = {
    id_colocacion,
    dias
  }
  return request({
    url: '/pegr/upd/' + data.id_colocacion + '/' + data.dias,
    method: 'get'
  })
}

export function agregarPeriodoGracia(id_colocacion, fecha, dias) {
  const data = {
    id_colocacion,
    fecha,
    dias
  }
  return request({
    url: '/pegr/add/' + data.id_colocacion + '/' + data.fecha + '/' + data.dias,
    method: 'get'
  })
}

export function eliminarPeriodoGracia(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: '/pegr/elim/' + data.id_colocacion,
    method: 'get'
  })
}
