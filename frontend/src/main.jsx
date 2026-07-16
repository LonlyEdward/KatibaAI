import React from "react";
import ReactDOM from "react-dom/client";

import { BrowserRouter } from "react-router-dom";

import { ApolloProvider } from "@apollo/client";

import apolloClient from "./api/graphql/apolloClient";

import { AuthProvider } from "./contexts/auth/";
import { ThemeProvider } from "./contexts/theme/ThemeProvider";

import App from "./App";

import "./styles/reset.css";
import "./styles/variables.css";
import "./styles/global.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <ApolloProvider client={apolloClient}>
        <AuthProvider>
          <ThemeProvider>
            <App />
          </ThemeProvider>
        </AuthProvider>
      </ApolloProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
