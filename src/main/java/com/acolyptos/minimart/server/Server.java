package com.acolyptos.minimart.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.sun.net.httpserver.HttpServer;

public class Server {
  public static void start() {
    try {
      DatabaseProvider mongoDB = new MongoDB();

      HttpServer httpServer = 
        HttpServer.create(new InetSocketAddress(8080), 0);

      // Register routes to the httpServer
      Router.registerRoutes(httpServer);

      httpServer.start();
      System.out.println("Server is running at http://localhost:8080");

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        mongoDB.closeConnection();
        httpServer.stop(0);
        System.out.println("Server stopped and MongoDB connection is closed.");
      }));
    } catch (IOException e) {
      System.err.println("Failed to start the server - " + e.getMessage());
    }
  }
}
