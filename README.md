# Tiny Tomcat
Simple project to build a basic cross-platform embedded Apache Tomcat jar for serving static files.

Tine Tomcat will build to a single jar file that can be used to serve static files over http.  

## Usage

`mvn package`

`cd target`

`java -jar tinytomcat-*.jar [web-root-path(default=.)] [port(default=8080)]`