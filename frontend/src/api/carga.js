import request from "@/utils/request";

export function getTodos() {
  return request({
    url: "/carga/todos",
    method: "get"
  });
}

export function eliminar(anho, mes) {
  return request({
    url: `/carga/delete/${anho}/${mes}`,
    method: "get"
  });
}

export function procesar01() {
  return request({
    url: "/carga/proc01",
    method: "get"
  });
}

export function procesar02() {
  return request({
    url: "/carga/proc02",
    method: "get"
  });
}

export function procesar03() {
  return request({
    url: "/carga/proc03",
    method: "get"
  });
}

export function procesar04() {
  return request({
    url: "/carga/proc04",
    method: "get"
  });
}
