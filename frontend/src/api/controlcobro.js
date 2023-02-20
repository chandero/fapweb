import request from "@/utils/request";

export function buscarDireccionPersona(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  };
  return request({
    url: "/coco/bdp/" + data.id_identificacion + "/" + data.id_persona,
    method: "get"
  });
}

export function buscarCreditoPorDocumento(id_identificacion, id_persona) {
  const data = {
    id_identificacion,
    id_persona
  };
  return request({
    url: "/coco/bpd/" + data.id_identificacion + "/" + data.id_persona,
    method: "get"
  });
}

export function buscarCreditoPorEstado(es, fi, ff, ases_id) {
  const data = {
    es,
    fi,
    ff,
    ases_id
  };
  return request({
    url:
      "/coco/bpe/" +
      data.es +
      "/" +
      data.fi +
      "/" +
      data.ff +
      "/" +
      data.ases_id,
    method: "get"
  });
}

export function buscarCreditoPorColocacion(id_colocacion) {
  const data = {
    id_colocacion
  };
  return request({
    url: "/coco/bpc/" + data.id_colocacion,
    method: "get"
  });
}

export function buscarControlCobro(id) {
  const data = {
    id
  };
  return request({
    url: "/coco/bcc/" + data.id,
    method: "get"
  });
}

export function agregarControlCobro(cc) {
  const data = {
    cc
  };
  return request({
    url: "/coco/ccs",
    method: "post",
    data: data.cc
  });
}

export function formatoPazySalvo(id) {
  const data = {
    id
  };
  return request({
    url: "/coco/fpys/" + data.id,
    method: "get",
    responseType: "blob"
  });
}

export function cartaPrimerAviso(credito, deudor, codeudor, tipo, email) {
  const data = {
    credito,
    deudor,
    codeudor,
    tipo,
    email
  };
  return request({
    url: "/coco/npa",
    method: "post",
    data: data
  });
}
