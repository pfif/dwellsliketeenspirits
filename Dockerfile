FROM openjdk:11

ADD target/server-standalone.jar server.jar

EXPOSE 80

CMD java -jar server.jar
