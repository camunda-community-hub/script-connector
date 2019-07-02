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

## Install

1) Download the [JAR file](https://github.com/zeebe-io/zeebe-script-worker/releases) 

2) Execute the JAR via

    `java -jar target/zeebe-script-worker-{VERSION}.jar`

### Configuration

The connection can be changed by setting the environment variables:
* `zeebe.client.broker.contactPoint` (default: `127.0.0.1:26500`).

## Build from Source

Build with Maven:
    
`mvn clean install`

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
