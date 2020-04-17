import request from '@/utils/request'

export function buscarDireccionPersona(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  }
  return request({
    url: '/coco/bdp/' + data.id_identificacion + '/' + data.id_persona,
    method: 'get'
  })
}

export function buscarCreditoPorDocumento(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  }
  return request({
    url: '/coco/bpd/' + data.id_identificacion + '/' + data.id_persona,
    method: 'get'
  })
}

export function buscarCreditoPorEstado(es, fi, ff, ases_id) {
  const data = {
    es,
    fi,
    ff,
    ases_id
  }
  return request({
    url: '/coco/bpe/' + data.es + '/' + data.fi + '/' + data.ff + '/' + data.ases_id,
    method: 'get'
  })
}
