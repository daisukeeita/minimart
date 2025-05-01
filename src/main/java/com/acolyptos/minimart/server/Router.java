package com.acolyptos.minimart.server;

import com.acolyptos.minimart.handlers.ProductRegisterHandler;
import com.acolyptos.minimart.handlers.UserLoginHandler;
import com.acolyptos.minimart.handlers.UserRegisterHandler;
import com.sun.net.httpserver.HttpServer;

public class Router {
  public static void registerRoutes(HttpServer server) {
    server.createContext("/api/login", new UserLoginHandler());
    server.createContext("/api/register", new UserRegisterHandler());
    server.createContext("/api/addProduct", new ProductRegisterHandler());
  }
}
