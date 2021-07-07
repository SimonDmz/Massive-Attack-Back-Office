FROM openjdk:11-jre-slim

ADD ./target/*.jar app.jar
ADD ./glowroot .


ENTRYPOINT ["java","-jar -javaagent:glowroot.jar","app.jar"]
