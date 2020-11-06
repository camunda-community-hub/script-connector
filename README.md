# zeebe-script-worker

A Zeebe worker to evaluate scripts (i.e. script tasks). Scripts are useful for prototyping, to do (simple) calculations, or creating/modifying variables.

## Usage

Example BPMN with service task:

```xml
<bpmn:serviceTask id="scripting" name="Evaluate the Script">
  <bpmn:extensionElements>
    <zeebe:taskDefinition type="script" />
    <zeebe:taskHeaders>
      <zeebe:header key="language" value="javascript" />
      <zeebe:header key="script" value="a + b" />
    </zeebe:taskHeaders>
  </bpmn:extensionElements>
</bpmn:serviceTask>
```

* the worker is registered for the type `script`
* required custom headers:
  * `language` - the name of the script language
  * `script` - the script to evaluate
* available context/variables in script:
  * `job` (ActivatedJob) - the current job
  * `zeebeClient` (ZeebeClient) - the client of the worker
* the result of the evaluation is passed as `result` variable   

Available script languages:
* javascript (Oracle Nashorn)
* [groovy](http://groovy-lang.org/)
* [feel](https://github.com/camunda/feel-scala)
* [mustache](http://mustache.github.io/mustache.5.html)
* [kotlin](https://kotlinlang.org/)

## Install

### Docker

The docker image for the worker is published to [DockerHub](https://hub.docker.com/r/camunda/zeebe-script-worker).

```
docker pull camunda/zeebe-script-worker:latest
```
* configure the connection to the Zeebe broker by setting `zeebe.client.broker.contactPoint` (default: `localhost:26500`) 

For a local setup, the repository contains a [docker-compose file](docker/docker-compose.yml). It starts a Zeebe broker and the worker. 

```
cd docker
docker-compose up
```

### Manual

1. Download the latest [worker JAR](https://github.com/zeebe-io/zeebe-script-worker/releases) _(zeebe-script-worker-%{VERSION}.jar
)_

1. Start the worker
    `java -jar zeebe-script-worker-{VERSION}.jar`

### Configuration

The worker is a Spring Boot application that uses the [Spring Zeebe Starter](https://github.com/zeebe-io/spring-zeebe). The configuration can be changed via environment variables or an `application.yaml` file. See also the following resources:
* [Spring Zeebe Configuration](https://github.com/zeebe-io/spring-zeebe#configuring-zeebe-connection)
* [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)

```
zeebe:
  client:
    worker:
      defaultName: script-worker
      defaultType: script
      threads: 3

    job.timeout: 10000
    broker.contactPoint: 127.0.0.1:26500
    security.plaintext: true
```

## Build from Source

Build with Maven

`mvn clean install`

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
