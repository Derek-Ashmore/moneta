#####################################
#
#   Moneta -- dropwizard deployment
#
#	Exposed ports:		8080 and 8081 (dropwizard admin)
#	Exposed Volumes:
#		/config		Expected to contain moneta.xml and moneta-dropwizard.yaml
#		/lib		Expected to contain additional jars needed (e.g. JDBC drivers)
#		/logs		Expected log file output (optional - logging configured in moneta-dropwizard.yaml)
#
#		Note:  Containers containing relational databases required by your configuration should be linked.
#
#		Sample usage:
#
#		docker image dropwizard .
#       docker run -d -p 8080:8080 -p 8081:8081 \ 
#			-v /c/Users/moneta/dropwizard/config:/config \ 
#			-v /c/Users/moneta/dropwizard/logs:/logs \ 
#			-v /c/Users/moneta/dropwizard/jarlib:/jarlib \ 
#			dropwizard
#

FROM java:8-jre

MAINTAINER Derek C. Ashmore

#  External volume definitions
RUN mkdir /jarlib
VOLUME /jarlib
RUN mkdir /config
VOLUME /config
RUN mkdir /logs
VOLUME /logs

ENV MONETA_URL https://github.com/Derek-Ashmore/moneta/releases/download/moneta-#{project.version}/moneta-dropwizard-#{project.version}.jar
RUN curl -SL "$MONETA_URL" -o moneta-dropwizard.jar

ENV CLASSPATH /config:/jarlib/*.jar:moneta-dropwizard.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-classpath", "$CLASSPATH", "-jar", "moneta-dropwizard.jar", "server", "/config/moneta-dropwizard.yaml"]