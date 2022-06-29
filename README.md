# Kafka Service

## Description

This project is a sample project to build services to an event-driven architecture.

It uses Kafka to produce and consume events with an Avro schema and implements a Dead Letter Publisher solution to handle 'poison pill' in kafka environment.

A poison pill in Kafka is a record that has been produced on a Kafka topic and always fails to be consumed. It can happen basically for 2 reasons:
* A corrupted record
* A deserialization failure

### Kafka Topics
* Default Topic: `me.hl.message`
* Dead Letter Topic: `me.hl.message.DLT`

### Technical Info

* Technologies:
    * Java
    * Spring Boot
    * Spring Kafka
    * Avro
    * Sleuth
    * Lombok
    * Docker Compose
    * Gradle

## Building from Source

First access the project root folder (kafka-service).

To clean/build the project from the console use the command: 

```console
    ./gradlew clean build
```

The build command will already generate the Java classes from Avro. 
If for some reason you need just to generate these classes without building use the command:

```console
    ./gradlew generateAvroJava
```

To run only tests from the console, use the command:

```console
    ./gradlew test
```

OBS: You can also use your IDE to run Gradle tasks.

## Using Docker Compose

To prepare all services necessaries, access the `docker` folder in project root and run the command:

```console
    docker-compose up -d
```
OBS: You need to have docker installed.

After that, the services will be available in a docker container.

Useful Commands:
* Start Container: `docker-compose up -d {{service_name}}`
* Stop Container: `docker-compose down -d {{service_name}}`
* Logs: `docker-compose logs {{service_name}}`
* Logs in Real Time: `docker-compose logs -f {{service_name}}`
* Access a docker service bash to run commands: `docker exec -it {{service_name}} /bin/bash`

### Kafka Control Service

After running docker compose, you can access Kafka Control Center with the URL: http://localhost:9021 to see Kafka information.

## Running the project

To run this project locally, the first thing you need to do is prepare your Kakfa environment.

You can do that by using docker compose on docker folder as detailed in `Using Docker Compose` session of this document.

With your kafka already prepared you just use the command below to run the project:

```console
    ./gradlew bootRun
```

OBS: You can also use your IDE to run the Project.

## Rest Clients

### Producing MessageCreatedEvents

This Rest client will use kafka to produce a message on topic: `me.hl.message`

Curl example:
```console
    curl --request POST \
      --url http://localhost:9102/api/v1/kafka/publish \
      --header 'Content-Type: application/json' \
      --data '{
        "key": "fb9240b9-24e8-4be9-bcd8-32363c631f0c",
        "message": {
            "senderName": "Name fb9240b9-24e8-4be9-bcd8-32363c631f0c",
            "content": {
                "title": "Title Message fb9240b9-24e8-4be9-bcd8-32363c631f0c",
                "body": "Body Message fb9240b9-24e8-4be9-bcd8-32363c631f0c"
            }
        }
}'
```

### Producing a "Poison Pill" Message

This Rest client will produce a message on topic that doesn't follow the Avro schema: `me.hl.message`

The consumer won't be able to deserialize the message, since its content doesn't follow the Avro existing schema and will produce a message to topic: `me.hl.message.DLT`.

Curl example:
```console
    curl --request POST \
      --url http://localhost:9102/api/v1/kafka/poison \
      --header 'Content-Type: text/plain' \
      --data 'my test string'
```

## Consideration
Project tests needs to improve coverage. 

## Reference
https://www.confluent.io/blog/spring-kafka-can-your-kafka-consumers-handle-a-poison-pill/
