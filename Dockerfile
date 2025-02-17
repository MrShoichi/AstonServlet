FROM tomcat:11.0

COPY target/aston-films-1.0.war /usr/local/tomcat/webapps/ROOT.war
COPY ./src/main/resources/ddl.sql /docker-entrypoint-initdb.d/
COPY ./src/main/resources/dml.sql /docker-entrypoint-initdb.d/

RUN chmod -R 755 /usr/local/tomcat/webapps/
RUN chown -R root:root /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]
