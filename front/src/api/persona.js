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
