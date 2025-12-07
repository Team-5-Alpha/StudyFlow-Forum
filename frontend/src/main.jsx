import React from "react";
import ReactDOM from "react-dom/client";

import Providers from "./app/Providers.jsx";
import App from "./app/App.jsx";

import "./index.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <Providers>
      <App />
    </Providers>
  </React.StrictMode>
);