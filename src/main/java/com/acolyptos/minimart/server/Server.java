package com.acolyptos.minimart.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.acolyptos.minimart.database.MongoDB;
import com.sun.net.httpserver.HttpServer;

public class Server {
  public static void start() {
    try {
      MongoDB.connect();

      HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
      Router.registerRoutes(httpServer); // Register routes to the httpServer

      httpServer.start();
      System.out.println("Server is running at http://localhost:8080");

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        MongoDB.closeConnection();
        httpServer.stop(0);
        System.out.println("Server stopped and MongoDB connection is closed.");
      }));
    } catch (IOException e) {
      System.err.println("Failed to start the server - " + e.getMessage());
    }
  }
}
