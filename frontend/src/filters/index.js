// set function parseTime,formatTime to filter
export { parseTime, formatTime } from '@/utils'

export function pluralize(time, label) {
  if (time === 1) {
    return time + label
  }
  return time + label + 's'
}

export function pad(num, size) {
  var s = num+"";
  while (s.length < size) s = "0" + s;
  return s;
}

export function timeAgo(time) {
  const between = Date.now() / 1000 - Number(time)
  if (between < 3600) {
    return pluralize(~~(between / 60), ' minutos')
  } else if (between < 86400) {
    return pluralize(~~(between / 3600), ' hora')
  } else {
    return pluralize(~~(between / 86400), ' día')
  }
}

export function numberFormatter(num, digits) {
  const si = [
    { value: 1E18, symbol: 'E' },
    { value: 1E15, symbol: 'P' },
    { value: 1E12, symbol: 'T' },
    { value: 1E9, symbol: 'G' },
    { value: 1E6, symbol: 'M' },
    { value: 1E3, symbol: 'k' }
  ]
  for (let i = 0; i < si.length; i++) {
    if (num >= si[i].value) {
      return (num / si[i].value + 0.1).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, '$1') + si[i].symbol
    }
  }
  return num.toString()
}

export function toThousandFilter(num) {
  return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}

export function fm_truncate(value, length) {
  if (!value) return ''
  value = value.toString()
  if (value.length > length) {
    return value.substring(0, length) + '...'
  } else {
    return value
  }
}

export function codigopuc(value) {
  if (value.length === 18) {
    if (value.substring(16) === '00') {
      value = value.substring(0,16)
    }
  }
  if (value.length === 16) {
    if (value.substring(14) === '00') {
      value = value.substring(0,14)
    }
  }  
  if (value.length === 14) {
    if (value.substring(12) === '00') {
      value = value.substring(0,12)
    }
  }  
  if (value.length === 12) {
    if (value.substring(10) === '00') {
      value = value.substring(0,10)
    }
  }
  if (value.length === 10) {
    if (value.substring(8) === '00') {
      value = value.substring(0,8)
    }
  }  
  if (value.length === 8) {
    if (value.substring(6) === '00') {
      value = value.substring(0,6)
    }
  }  
  if (value.length === 6) {
    if (value.substring(4) === '00') {
      value = value.substring(0,4)
    }
  }  
  if (value.length === 4) {
    if (value.substring(2) === '00') {
      value = value.substring(0,2)
    }
  }  
  if (value.length === 2) {
    if (value.substring(1) === '0') {
      value = value.substring(0,1)
    }
  }  
  return value;
}
