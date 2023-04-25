const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
  entry: './index.js',
  output: {
    filename: 'fn-crypto.js',
    path: path.resolve(__dirname, 'dist'),
    library: 'FNCryptoLib',
    libraryTarget: 'umd'
  },
  target: 'node',
  optimization: {
    minimize: true,
    minimizer: [new TerserPlugin()]
  },
  mode: 'development'
};
