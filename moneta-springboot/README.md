# Moneta Springboot Deployment

This is a Spring Boot/Docker deployment for Moneta.

To produce a Docker image artifact, run the install target with the Docker profile as shown below. The image produced is named with the pattern moneta-springboot-${project.version} and varies per version deployed.  Note that the environment variables DOCKER_HOST and DOCKER_CERT_PATH must be set for the Docker portion to work.
```  
mvn -P Docker install  
```  