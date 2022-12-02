import request from "@/utils/request";

export function enviarFactura(id) {
  const data = {
    id
  };
  return request({
    url: "/http/st/" + data.id,
    method: "get"
  });
}

export function enviarDSA(id) {
  const data = {
    id
  };
  return request({
    url: "/http/dsast/" + data.id,
    method: "get"
  });
}
