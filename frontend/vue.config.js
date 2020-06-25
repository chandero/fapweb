module.exports = {   
    devServer: {
        proxy: {
            '^/api': {
              target: 'http://localhost:9000',
              secure: false,
              changeOrigin: true,
              proxyTimeout: 0,
              timeout: 0,
              onProxyReq: (proxyReq, req, res) => req.setTimeout(0)
            },
            '^/ipa': {
              target: 'http://localhost:3000',
              pathRewrite:  {
                '^/ipa' : '/'
              },              
              secure: false,
              changeOrigin: true,
              proxyTimeout: 0,
              timeout: 0,
              onProxyReq: (proxyReq, req, res) => req.setTimeout(0)
            }
        },
        host: 'localhost',
        port: 80        
    }
}