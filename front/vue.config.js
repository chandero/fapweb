// vue.config.js
module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  chainWebpack: config => {
    config
      .plugin('html')
      .tap(args => {
        //  args[0] contains the plugin's options object
        // change it to what you need it to be.
        return args
      })
  }
}
