import request from '@/utils/request'

export function obtenerListaTipoIdentificacion() {
  return request({
    url: '/ti/obt',
    method: 'get'
  })
}

export function obtenerListaTipoPersona() {
  return request({
    url: '/tp/obt',
    method: 'get'
  })
}

export function obtenerListaTipoEstadoCivil() {
  return request({
    url: '/tec/obt',
    method: 'get'
  })
}

export function obtenerListaTipoNivelEscolaridad() {
  return request({
    url: '/tne/obt',
    method: 'get'
  })
}

export function obtenerListaTipoEstrato() {
  return request({
    url: '/tet/obt',
    method: 'get'
  })
}

export function obtenerListaTipoVivienda() {
  return request({
    url: '/tvd/obt',
    method: 'get'
  })
}

export function obtenerListaTipoOcupacion() {
  return request({
    url: '/toc/obt',
    method: 'get'
  })
}

export function obtenerListaTipoContrato() {
  return request({
    url: '/tco/obt',
    method: 'get'
  })
}

export function obtenerListaTipoCiiu() {
  return request({
    url: '/tcu/obt',
    method: 'get'
  })
}

export function obtenerListaTipoDireccion() {
  return request({
    url: '/td/obt',
    method: 'get'
  })
}

export function obtenerListaTipoReferencia() {
  return request({
    url: '/tr/obt',
    method: 'get'
  })
}

export function obtenerListaTipoParentesco() {
  return request({
    url: '/tt/obt',
    method: 'get'
  })
}

export function obtenerListaMunicipios() {
  return request({
    url: '/muni/all',
    method: 'get'
  })
}
