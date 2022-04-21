FROM openjdk:11-jre-slim

RUN rm -rf $CATALINA_HOME/webapps/*
COPY sabdatab.properties log4j2.xml $CATALINA_HOME/webapps/
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war

ENTRYPOINT ["java","-javaagent:glowroot.jar","-jar","app.jar"]
