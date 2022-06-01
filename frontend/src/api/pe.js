import request from "@/utils/request";

export function loadData(anho, mes, uuid) {
  return request({
    url: `/pe/load/${anho}/${mes}/${uuid}`,
    method: "get"
  });
}

export function getDeterioro() {
  return request({
    url: "/pe/deter/getall",
    method: "get"
  });
}

export function getEdad() {
  return request({
    url: "/pe/edad/getall",
    method: "get"
  });
}

export function getGarantia() {
  return request({
    url: "/pe/garan/getall",
    method: "get"
  });
}

export function getTipoCartera() {
  return request({
    url: "/pe/tipcar/getall",
    method: "get"
  });
}

export function getTipoGarantia() {
  return request({
    url: "/pe/tipgar/getall",
    method: "get"
  });
}

export function getVarIndependiente() {
  return request({
    url: "/pe/varind/getall",
    method: "get"
  });
}
