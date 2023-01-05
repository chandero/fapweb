import request from '@/utils/request'

export function crearNotaDebito (nota) {
  const data = {
    nota
  }
  return request({
    url: '/fact/cnf',
    method: 'post',
    data: data.nota
  })
}

export function crearNotaCredito (nota) {
  const data = {
    nota
  }
  return request({
    url: '/fact/cnf',
    method: 'post',
    data: data.nota
  })
}

export function crearNotaCreditoDSA (nota) {
  const data = {
    nota
  }
  return request({
    url: '/fact/cnf',
    method: 'post',
    data: data.nota
  })
}

export function getFactura(fact_numero) {
    const data = {
        fact_numero
    }
    return request({
        url: '/fact/bpn/' + data.fact_numero,
        method: 'get'
    })
}

export function getDSA(fact_numero) {
  const data = {
      fact_numero
  }
  return request({
      url: '/fact/dsabpn/' + data.fact_numero,
      method: 'get'
  })
}

export function getFacturaProveedor(fact_numero, periodo) {
  const data = {
    fact_numero,
    periodo
  }
  return request({
    url: '/http/gtbi/FAP' + data.fact_numero + '/' + data.periodo + '/' + '804015942',
    method: 'get'
  })
}

export function getDSAProveedor(fact_numero, periodo) {
  const data = {
    fact_numero,
    periodo
  }
  return request({
    url: '/http/gtbi/DSE' + data.fact_numero + '/' + data.periodo + '/' + '804015942',
    method: 'get'
  })
}

export function getFacturas(page_size, current_page, filter, orderby) {
    const data = {
      page_size,
      current_page,
      orderby,
      filter
    }
    return request({
      url: '/fact/fcget',
      method: 'post',
      data: data
    })
}

export function getDSAs(page_size, current_page, filter, orderby) {
  const data = {
    page_size,
    current_page,
    orderby,
    filter
  }
  return request({
    url: '/fact/dsaget',
    method: 'post',
    data: data
  })
}

export function getNotasDebito(page_size, current_page, filter, orderby) {
    const data = {
        page_size,
        current_page,
        orderby,
        filter
    }
    return request({
      url: '/fact/ndget',
      method: 'post',
      data: data
    })
}

export function getNotasCredito(page_size, current_page, filter, orderby) {
    const data = {
        page_size,
        current_page,
        orderby,
        filter
    }
    return request({
      url: '/fact/ncget',
      method: 'post',
      data: data
    })
}

export function getNotasCreditoDSA(page_size, current_page, filter, orderby) {
  const data = {
      page_size,
      current_page,
      orderby,
      filter
  }
  return request({
    url: '/fact/ncsget',
    method: 'post',
    data: data
  })
}

export function enviarFactura(fact_numero) {
  const data = {
    fact_numero
  }
  return request({
    url: '/http/st/' + data.fact_numero,
    method: 'get'
  })
}

export function enviarDSA(fact_numero) {
  const data = {
    fact_numero
  }
  return request({
    url: '/http/dsast/' + data.fact_numero,
    method: 'get'
  })
}
export function enviarNotaDebito(fact_nota_numero) {
  const data = {
    fact_nota_numero
  }
  return request({
    url: '/http/stnd/' + data.fact_nota_numero,
    method: 'get'
  })
}

export function enviarNotaCredito(fact_nota_numero) {
  const data = {
    fact_nota_numero
  }
  return request({
    url: '/http/stnc/' + data.fact_nota_numero,
    method: 'get'
  })
}

export function enviarNotaCreditoDSA(fact_nota_numero) {
  const data = {
    fact_nota_numero
  }
  return request({
    url: '/http/stncs/' + data.fact_nota_numero,
    method: 'get'
  })
}