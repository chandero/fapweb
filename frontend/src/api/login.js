import request from '@/utils/request'

export function loginByUsername(username, password) {
  const data = {
    username,
    password
  }
  return request({
    url: '/u/a/' + data.username + '/' + data.password,
    method: 'get'
  })
}

export function logout(token) {
  return request({
    url: '/login/logout',
    method: 'post',
    data: token
  })
}

export function getUserInfo() {
  return request({
    url: '/u/ui',
    method: 'get'
  })
}

