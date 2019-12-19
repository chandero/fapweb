package http

import requests._

class HttpClient {
    def doGet(url: String, parametros: Map[String,String] ): Response = {
        val r = requests.get(url, params = parametros)
        r
    }

    def doPost(url: String, json: String): Response = {
        val r = requests.post(url, data = json)
        r
    }
 }