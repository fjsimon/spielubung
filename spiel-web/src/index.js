import React from 'react';
import ReactDOM from 'react-dom';
import './styles/main.scss';
import App from './component/App';

ReactDOM.render(<App />, document.getElementById('root'));

// Keep code below for Webpack Hot Module Replacement to work correctly
if (module.hot) {
  module.hot.accept();
}
