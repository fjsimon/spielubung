const path = require('path');

const BrowserSyncPlugin = require('browser-sync-webpack-plugin'); // Browser Sync plugin for Webpack  to allow for external url for testing
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer'); // Bundle Analyzer to show contents and sizes of compiled JavaScript bundles
const { CleanWebpackPlugin } = require('clean-webpack-plugin'); // Clean Webpack plugin to remove contents of existing dist folder prior to new build
const CompressionWebpackPlugin = require('compression-webpack-plugin'); // Compress assets
const HtmlWebpackPlugin = require('html-webpack-plugin'); // HTML Webpack plugin to create index.html file per provided template
const MiniCssExtractPlugin = require('mini-css-extract-plugin'); // Splits CSS into separate files in production mode
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin'); // Minimize CSS assets during build (includes use of cssnano)
//const UglifyjsWebpackPlugin = require('uglifyjs-webpack-plugin'); // Minify JavaScript files
const TerserPlugin = require('terser-webpack-plugin'); // Minify JavaScript files

module.exports = (env, arg) => {
  // Everything that's the same in dev and prod goes in this config variable
  const config = {
    // Don't need an entry setting if using index.js in the root directory as the entry point
    output: {
      path: path.resolve(__dirname, 'dist'),
      // Use chunkhash in production build only to avoid problems with HotModuleReplacement in development
      filename:
        arg.mode === 'production' ? '[name].[chunkhash].js' : '[name].js',
    },
    module: {
      rules: [
        {
          // Run JavaScript thru Babel
          test: /\.jsx?$/,
          exclude: /node_modules/,
          use: ['babel-loader'],
        },
        {
          // Compile Sass to CSS
          test: /\.scss$/,
          use: [
            {
              loader:
                // Use MiniCssExtractPlugin in production mode and style-loader in development mode
                arg.mode === 'production'
                  ? MiniCssExtractPlugin.loader
                  : 'style-loader',
            },
            {
              loader: 'css-loader',
              options: {
                sourceMap: arg.mode === 'development',
                importLoaders: 2,
              },
            },
            { loader: 'postcss-loader' },
            {
              loader: 'sass-loader',
              options: {
                sourceMap: arg.mode === 'development',
              },
            },
          ],
        },
        {
          test: /\.(jpe?g|png|gif)$/,
          use: [
            {
              // url-loader doesn't work with svg
              loader: 'url-loader',
              options: {
                fallback: 'file-loader',
                limit: 8192,
                outputPath: 'images',
                name: '[hash:8].[ext]',
              },
            },
            {
              loader: 'image-webpack-loader',
              options: {},
            },
          ],
        },
        {
          // url-loader doesn't work with svg
          test: /\.svg$/,
          use: [
            {
              loader: 'file-loader',
              options: {
                outputPath: 'images/',
                name: '[hash:8].[ext]',
              },
            },
            {
              loader: 'image-webpack-loader',
              options: {},
            },
          ],
        },
      ],
    },
    plugins: [
      // Create template index.html
      new HtmlWebpackPlugin({
        template: './src/index.html',
      }),
    ],
  };

  if (arg.mode === 'development') {
    config.devtool = 'eval-source-map';
    config.devServer = {
      compress: true,
      // Set "base content directory" to src and tell devServer to watch it for changes. This allows hot reloading of index.html
      contentBase: 'src',
      watchContentBase: true,
    };
    config.plugins.push(
      // set up browsersync to work with Webpack devServer via proxy
      new BrowserSyncPlugin(
        {
          host: 'localhost',
          port: 3000,
          proxy: 'http://localhost:8080/',
        },
        { reload: false },
      ),
      // Remove comments for BundleAnalyzerPlugin when its use is desired
      // new BundleAnalyzerPlugin(),
    );
  }

  if (arg.mode === 'production') {
    config.optimization = {
      splitChunks: {
        cacheGroups: {
          // Split off npm package code into separate chunk
          vendor: {
            test: /node_modules/,
            name: 'vendors',
            chunks: 'all',
          },
        },
      },
      minimizer: [
          new TerserPlugin({
            test: /\.js(\?.*)?$/i,
            parallel: true,
            cache: true,
            sourceMap: true,
          })
      ],
    };
    config.plugins.push(
      new MiniCssExtractPlugin({}),
      new CleanWebpackPlugin(),
      new CompressionWebpackPlugin({
        test: /\.(jsx?|html)$/,
      }),
      new OptimizeCssAssetsPlugin({}),
      // Remove comments for BundleAnalyzerPlugin when its use is desired
      // new BundleAnalyzerPlugin()
    );
  }

  return config;
};
