import request from '@/utils/request'

export function buscarCredito(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  }
  return request({
    url: '/incr/busc/' + data.id_identificacion + '/' + data.id_persona,
    method: 'get'
  })
}