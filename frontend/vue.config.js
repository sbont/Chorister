module.exports = {
    runtimeCompiler: true,
    devServer: {
      port: 8091
    },
    chainWebpack: config => {
        config.resolve.alias.set('vue', '@vue/compat')
        config.module
            .rule('vue')
            .use('vue-loader')
            .tap(options => {
                return {
                    ...options,
                    compilerOptions: {
                        compatConfig: {
                            MODE: 2
                        }
                    }
                }
            })
        config
            .plugin('html')
            .tap(args => {
                args[0].title = "Chorister";
                return args;
            })
    }
  };