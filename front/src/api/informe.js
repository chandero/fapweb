import request from '@/utils/request'

export function genLibroMayor(periodo, anho, definitivo) {
  const data = {
    periodo,
    anho,
    definitivo
  }
  return request({
    url: '/info/genlm/' + data.periodo + '/' + data.anho,
    method: 'get'
  })
}

export function conLibroMayor(lire_anho) {
  const data = {
    lire_anho
  }
  return request({
    url: '/info/conlm/' + data.lire_anho,
    method: 'get'
  })
}

export function verLibroMayor(lire_anho, lire_periodo, lire_consecutivo) {
  const data = {
    lire_anho,
    lire_periodo,
    lire_consecutivo
  }
  const url = window.location.protocol + '//' + window.location.host.split('/')[0].split(':')[0] + ':9000/api' + '/info/verlm/' + data.lire_anho + '/' + data.lire_periodo + '/' + data.lire_consecutivo
  window.open(url, '_self', 'location=no, menubar=no')
}

export function consultarClienteBuenPago() {
  return request({
    url: '/info/ccbp',
    method: 'get'
  })
}

export function generarClienteBuenPago() {
  return request({
    url: '/info/gcbp',
    method: 'post',
    timeout: 1200000
  })
}

export function exportarClienteBuenPago() {
  const url = window.location.protocol + '//' + window.location.host.split('/')[0].split(':')[0] + ':9000/api' + '/info/ecbp'
  window.open(url, '_self', 'location=no, menubar=no')
}

export function consultarCausacion(id_colocacion) {
  const data = {
    id_colocacion
  }
  return request({
    url: 'info/conc/' + data.id_colocacion,
    method: 'get'
  })
}

export function extractoCausacion(id_colocacion, fecha_inicial, fecha_final) {
  const data = {
    id_colocacion,
    fecha_inicial,
    fecha_final
  }
  return request({
    url: 'info/extc/' + data.id_colocacion + '/' + data.fecha_inicial + '/' + data.fecha_final,
    method: 'get'
  })
}

export function exportarCausacion(id_colocacion, empr_id, token) {
  const data = {
    id_colocacion,
    empr_id,
    token
  }
  const url = window.location.protocol + '//' + window.location.host.split('/')[0].split(':')[0] + ':9000/api' + '/info/expc/' + data.id_colocacion + '/' + data.empr_id + '/' + data.token
  window.open(url, '_self', 'location=no, menubar=no')
}