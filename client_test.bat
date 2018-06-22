@echo off

java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -d 2 -c bla -p 8080 -a 127.0.0.1 -m 0
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -d 2 -c bla -p 8080 -a 127.0.0.1 -m 1
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -d 2 -c bla -p 8080 -a 127.0.0.1 -m 3
:: Create duplicate message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -d 2 -c bla -p 8080 -a 127.0.0.1 -m 0


java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -p 8080 -a 127.0.0.1 -m 3

:: Clear message and read the content
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -p 8080 -a 127.0.0.1 -m 2
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -p 8080 -a 127.0.0.1 -m 3


java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -n Test -p 8080 -a 127.0.0.1 -m 5
