#!/bin/bash

# Create a message, write something into it and read the content
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 0 -n Test -d 2
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 1 -n Test -d 2 -c bla
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test
echo

# Set a different content and read it
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 1 -n Test -d 2 -c "Something completely different!"
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test
echo

# Create a duplicate message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 0 -n Test -d 2
echo

#Read a not existing messages
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n test
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test2
echo

# Clear a not existing message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 2 -n Test
echo

# Clear message and read the content
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 2 -n Test
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test
echo

# Delete the message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 5 -n Test
echo

# Delete the message a second time
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 5 -n Test
echo

# Read the deleted message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test
echo

# Use an unknown method
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 6 -n Test
echo
