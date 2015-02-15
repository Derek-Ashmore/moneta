# Moneta
Moneta is a microservice that provides a RESTful web service interface to a relational database.  This project is intended as a *sample* that illustrates a microservice written using Java EE.

For those who are curious, *Moneta* is a greek goddess of memory.

## Features
* Provides generic ‘core’ services
* Returns Json-formatted data
* startRow query option:  Will return results **starting** at the row indicated
* maxRows query option:  Will limit the number of records returned
* Supports a security call-out

## Deployments
Moneta comes with sample deployments for Dropwizard, Spring Boot, and a Web (war) deployment.

## Sample Request URL examples
These examples 'assume' a configured topic of Customer  
* /moneta/topics  
--  Provides a list of *topics* available to display.  In this case, *Customer* is one of them.  
* /moneta/topic/customers?startRow=5&maxRows=25  
--  Lists customers starting with 5th and proceeding for a maxiumum of 25 entries  
* /moneta/topic/customer/111-222-333  
--  Lists detail for customer with id 111-222-333  

All results are returned in a JSon format.

## Supported Databases
* HsqlDB
