import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import store from "./store/index";
import App from "./component/App";
import './styles/main.scss';

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById("root")
);

// Keep code below for Webpack Hot Module Replacement to work correctly
if (module.hot) {
  module.hot.accept();
}
