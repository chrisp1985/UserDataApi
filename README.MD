# User Data API #

## Background ##
This is a demonstration of WebFlux using a fairly basic Spring Boot controller to show how to interact with a database in 
a non-blocking (async) way.

To execute:
- Build with Gradle using `./gradlew clean build`.
- Dockerise with `./gradlew dockerBuildImage`.
- Then run the docker-compose file with `docker-compose up` in the docker directory.
- Execute flyway migration with `./gradlew flywayMigrate`.

## Spring Boot ##
Spring Boot uses WebFlux, but for WebFlux to work with a MySQL database in a non-blocking way we also need to use an R2DBC
connector rather than a standard JDBC. We return Mono (for zero or one items returned) or Flux (for zero or many). Reactive
APIs work by taking the request, fetching the results and freeing the current thread to be able to take on other requests.
This means a reactive API can handle throughput more efficiently, but if the connection to the database is single threaded
(like WebFlux calling through a JDBC connector), the reactive part is rendered pretty irrelevant.

## Test ##
To test WebFlux APIs, we need to use a WebTestClient rather than a RestTemplate, because the client is non-blocking. The unit tests
use a StepVerifier to verify the service, allowing the test to 'verify complete' which ends the asynchronous call.

The TestContainers run with the mysql container set up and populated. The test example shows how to validate that the cache is
working, checking that calls made to the database are stopped and the cache is used instead after the first call.

## Data Migrations - Flyway ##
Flyway migrations are defined in the resources directory of the main application under db/migration. Here, migrations are versioned
starting with V1__. Migrations will be run in order, so for any changes needed to the database you just add another file to the
set. This can be executed with `.\gradlew flywayMigrate` and will update the data in the database (run the docker compose first).

## Docker ##
The docker-compose file contains docker containers for MySQL, Prometheus, AlertManager and Grafana.

### MySQL ###
MySQL is booted up in a container as the backend database for the application. Flyway should be used to populate the data, then
the application can fetch the data from the database. Flyway connects using a JDBC connection, but the Spring Boot application
uses R2DBC to connect for asynchronous purposes. The mydb database can then be connected to locally using the 3306 port.

### Prometheus ###
Prometheus collects metrics from the service. It uses the service at `user-api-service:9092` and the yml [here](docker/prometheus/prometheus.yml).


### Alert Manager ###
Alert Manager isn't set up to do anything, but would normally evaluate Prometheus rules with any alerts being routed to the receiver
as set in the [alertmanager](docker/alertmanager/alertmanager.yml) yml file.

### Grafana ###
Grafana uses a default dashboard to display data for the springboot service. We can set up Prometheus as a data source, then import
the dashboard from Grafana from https://grafana.com/grafana/dashboards/11378. This then shows the metrics churned out from the 
service in real time.

## Helm ##
Helm files can be created using `helm create userdataapi`. This includes templates and helpers as well as the default value
file. Deployment strategies (ie for rolling upgrades) can be set in the [deployment](helm/userdataapi/templates/deployment.yaml) yaml.

### Fault Tolerance ###
The replicas in a production environment should be set to > 1 for resilience. This can be set to 1 on test, and eg 3 on Prod.

### Affinity ###
Setting the affinity makes sure that applications are distributed across nodes properly. This adds further fault tolerance, as
if a node goes down, the application should still be available on another node.

### Environments ###
The values.yaml will be used all the time, but for specific environments a separate prod-values or test-values yml can be created
pertaining to each. This can be executed in the github workflow or gitlab ci yml etc by running a Helm install or update like follows:
```helm
    helm -n <namespace> upgrade --install userdataapi -f userdataapi/prod-values.yaml userdataapi --dry-run --debug
    helm -n <namespace> upgrade --install userdataapi -f userdataapi/prod-values.yaml userdataapi --wait --debug
```