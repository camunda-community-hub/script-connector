# zeebe-script-worker

This is a Zeebe worker to evaluate scripts. Scripts are useful to create/modify the payload, to do (simple) calculations or for prototyping.

* the worker is registered for the type `script`
* required custom headers:
  * `language` (String) - the name of the script language
  * `script` (String) - the script to evaluate
* output payload contains `result` - the result of the evaluation   

Available script languages:
* javascript (Oracle Nashorn)
* [groovy](http://groovy-lang.org/)
* [feel](https://github.com/camunda/feel-scala)

_This is a community project meant for playing around with Zeebe. It is not officially supported by the Zeebe Team (i.e. no gurantees). Everybody is invited to contribute!_

## Usage

The service task:
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

The job's payload:
```json
{ "a": 2,
  "b": 3 }
```

The job's result payload:
```json
{ "result": 5 }
```

## Examples

### JavaScript

...

### FEEL

...

### Groovy

...

## How to build

Build with Maven

`mvn clean install`

## How to run

Execute the JAR file via

`java -jar target/zeebe-script-worker-{VERSION}.jar`

## How to configure

The worker can be configured via environment variables or a properties file `application.properties`.

```
zeebe.client.broker.contactPoint=127.0.0.1:51015
zeebe.client.topic=default-topic
```

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
