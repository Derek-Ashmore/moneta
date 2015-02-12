# Moneta
Moneta is a microservice that provides a RESTful web service interface to a relational database.  This project is intended as a *sample* that illustrates a microservice written using Java EE.

## Features
* Provides generic ‘core’ services
* Returns Json-formatted data
* startRow query option:  Will return results **starting** at the row indicated
* maxRows query option:  Will limit the number of records returned
* Supports a security call-out

## Deployments
Moneta comes with sample deployments for Dropwizard, Spring Boot, and a Web (war) deployment.

## Supported Databases
* HsqlDB
