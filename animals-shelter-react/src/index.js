import React, { createContext, useContext, useEffect, useState } from 'react';
import { createRoot } from "react-dom/client";
import { ApolloClient, ApolloProvider, InMemoryCache, HttpLink, ApolloLink } from "@apollo/client";
import { onError } from "@apollo/client/link/error";
import { NextUIProvider } from "@nextui-org/react";
import './output.css';
import App from "./app";
import './styles/global.css';

const apiUrl = process.env.REACT_APP_API_URL;

const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, locations, path }) => {
      console.error(
        `[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`
      );
      if (message === 'Unauthorized') {
        window.location.href = '/login';
      }
    });
  }

  if (networkError) {
    console.error(`[Network error]: ${networkError}`);
    window.location.href = '/login';
  }
});

const httpLink = new HttpLink({
  uri: `${apiUrl}/graphql`,
  credentials: 'include',
});

const client = new ApolloClient({
  link: ApolloLink.from([errorLink, httpLink]),
  cache: new InMemoryCache(),
});

const root = createRoot(document.getElementById("root"));
root.render(
  <ApolloProvider client={client}>
    <NextUIProvider>
      <App />
    </NextUIProvider>
  </ApolloProvider>
);
