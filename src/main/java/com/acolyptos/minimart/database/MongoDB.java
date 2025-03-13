package com.acolyptos.minimart.database;

import io.github.cdimascio.dotenv.Dotenv;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoDB {

  private static MongoClient mongoClient;
  private static MongoDatabase database;

  private final static String DB_NAME = "inventory_system";

  /*
   * Connects to the MongoDB database using the provided URI.
   */
  public static void connect() {
    Dotenv dotenv = Dotenv.load();
    final String URI = dotenv.get("MONGO_URI");

    if (mongoClient == null) {
      CodecRegistry pojoCodecRegistry = CodecRegistries
          .fromProviders(PojoCodecProvider.builder().automatic(true).build());

      CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
          MongoClientSettings.getDefaultCodecRegistry(),
          pojoCodecRegistry);

      MongoClientSettings settings = MongoClientSettings.builder()
          .applyConnectionString(new com.mongodb.ConnectionString(URI))
          .codecRegistry(codecRegistry)
          .build();

      mongoClient = MongoClients.create(settings);
      database = mongoClient.getDatabase(DB_NAME);

      System.out.println("Succesfully Connected to the MongoDB Atlas.");
    }
  }

  /*
   * Retrieves the connected MongoDB database instance.
   *
   * @return The connected MongoDatabase instance
   */
  public static MongoDatabase getDatabase() {
    return database;
  }

  /*
   * Closes the MongoDB client if it's open
   */
  public static void closeConnection() {
    if (mongoClient != null) {
      mongoClient.close();
      mongoClient = null;
      System.out.println("MongoDB connection closed.");
    }
  }
}
