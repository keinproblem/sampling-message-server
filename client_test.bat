@echo off

java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 0 -n Test -d 2
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 1 -n Test -d 2 -c bla
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test -d 2
:: Create duplicate message
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 0 -n Test -d 2


java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Moron

:: Clear message and read the content
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 2 -n Test
java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 3 -n Test


java -jar sampling-message-client/target/sampling-message-client-0.1-jar-with-dependencies.jar -p 8080 -a 127.0.0.1 -m 5 -n Test
