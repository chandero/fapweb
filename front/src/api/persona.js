import request from '@/utils/request'

export function obtenerPersona(i, p) {
  const data = {
    i,
    p
  }
  return request({
    url: '/pers/ver/' + data.i + '/' + data.p,
    method: 'get'
  })
}

export function obtenerPersonaPorColocacion(c) {
  const data = {
    c
  }
  return request({
    url: '/pers/bpc/' + data.c,
    method: 'get'
  })
}

export function guardarPersona(p) {
  const data = {
    p
  }
  return request({
    url: '/pers/gdr',
    method: 'post',
    data: data.p
  })
}

export function obtenerPersonaPorApellidosYNombre(primer_apellido, segundo_apellido, nombre) {
  const data = {
    primer_apellido,
    segundo_apellido,
    nombre
  }
  return request({
    url: 'pers/opn',
    method: 'post',
    data: data
  })
}
