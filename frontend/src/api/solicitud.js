import request from '@/utils/request'

export function guardarSolicitud(solicitud) {
  const data = {
    solicitud
  }
  return request({
    url: '/socr/save',
    method: 'post',
    data: data.solicitud
  })
}
