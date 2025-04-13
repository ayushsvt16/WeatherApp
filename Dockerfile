FROM tomcat:9.0-jdk17

RUN rm -rf /usr/local/tomcat/webapp/*

COPY webapp/ /usr/local/tomcat/webapp/ROOT/

EXPOSE 8080
CMD ["catalina.sh", "run"]
