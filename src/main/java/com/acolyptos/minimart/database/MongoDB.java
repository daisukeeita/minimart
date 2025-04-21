package com.acolyptos.minimart.database;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoDB implements DatabaseProvider {

  private static MongoClient mongoClient;
  private static MongoDatabase mongoDatabase;

  private final static Dotenv DOTENV = Dotenv.load();
  private final static String DB_NAME = DOTENV.get("DB_NAME");
  private final static String URI = DOTENV.get("MONGO_URI");

  private final static Logger LOGGER = LoggerFactory.getLogger(MongoDB.class);

  public MongoDB() {
    if (mongoClient == null) {
      CodecRegistry pojoCodecRegistry = CodecRegistries
      .fromProviders(PojoCodecProvider.builder()
        .automatic(true)
        .register("com.acolyptos.minimart.models")
        .build()
      );

      CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(pojoCodecRegistry)
      );

      MongoClientSettings settings = MongoClientSettings.builder()
      .applyConnectionString(new com.mongodb.ConnectionString(URI))
      .codecRegistry(codecRegistry)
      .build();

      mongoClient = MongoClients.create(settings);
      mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }
  }

  /*
   * Retrieves the connected MongoDB mongoDatabase instance.
   *
   * @return The connected MongoDatabase instance
   */
  @Override
  public MongoDatabase getDatabase() {
    return mongoDatabase;
  }
  
  /*
   * Retrieves the connected MongoClient instance
   *
   * @return The connected MongoClient instance
   */
  @Override
  public MongoClient getMongoClient() {
    return mongoClient;
  }

  /*
   * Closes the MongoDB client if it's open
   */
  @Override
  public void closeConnection() {
    if (mongoClient != null) {
      mongoClient.close();
      mongoClient = null;
      mongoDatabase = null;
      LOGGER.info("MongoDB connection closed.");
    }
  }
}
