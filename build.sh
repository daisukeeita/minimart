#!/bin/bash

# Clean previous build files
mvn clean

# Compile the project
mvn compile

# Run the project quietly (only show println and errors)
mvn -q exec:java -Dexec.mainClass="com.acolyptos.minimart.Main"
