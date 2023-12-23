const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  // https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
      port: 3000,
      proxy: {
          '/api': {
              target: 'http://localhost:8084', // this is where spring boot runs; change accordingly
              ws: true,
              changeOrigin: true
          }
      }
  },
  transpileDependencies: [
    'vuetify'
  ],
  // publicPath: './' // if relative
})
