#####################################
#
#   Moneta -- Spring Boot deployment
#
#	Exposed ports:		8080 
#	Exposed Volumes:
#		/config		Expected to contain moneta.xml, logback.xml, and optionally application.properties
#		/lib		Expected to contain additional jars needed (e.g. JDBC drivers)
#		/logs		Expected log file output (optional - logback.xml)
#
#		Note:  Containers containing relational databases required by your configuration should be linked.
#
#		Sample usage:
#
#		docker build -t springboot .
#       docker run -d -p 8080:8080 -p 8081:8081 \ 
#			-v /c/Users/moneta/springboot/config:/config \ 
#			-v /c/Users/moneta/springboot/logs:/logs \ 
#			-v /c/Users/moneta/springboot/jarlib:/jarlib \ 
#			springboot
#

FROM java:7-jre

MAINTAINER Derek C. Ashmore

#  External volume definitions
COPY moneta-springboot.jar /
VOLUME /config
VOLUME /logs

ENV CLASSPATH moneta-springboot.jar

EXPOSE 8080

ENTRYPOINT ["java", "-classpath", "$CLASSPATH", "-jar", "moneta-springboot.jar"]