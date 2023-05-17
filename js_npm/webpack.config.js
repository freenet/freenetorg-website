const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
  entry: './index.js',
  output: {
    filename: 'freenetorg.js',
    path: path.resolve(__dirname, 'dist'),
    library: 'FNCryptoLib',
    libraryTarget: 'umd',
    globalObject: 'this', // This allows the library to be used in various environments.
  },
  target: 'web', // Change target to 'web' for browser environment
  optimization: {
    minimize: true,
    minimizer: [new TerserPlugin()]
  },
  mode: 'production' // Change to 'development' for development mode
};
