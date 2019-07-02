# zeebe-script-worker

A Zeebe worker to evaluate scripts. Scripts are useful for prototyping, to do (simple) calculations, or creating/modifying variables.

* the worker is registered for the type `script`
* required custom headers:
  * `language` (String) - the name of the script language
  * `script` (String) - the script to evaluate
* available context/variables in script:
  * `job` (ActivatedJob) - the current job
  * `zeebeClient` (ZeebeClient) - the client of the worker
* the result of the evaluation is passed as `result` variable   

Available script languages:
* javascript (Oracle Nashorn)
* [groovy](http://groovy-lang.org/)
* [feel](https://github.com/camunda/feel-scala)

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

## Install

1) Download the JAR file 

    or build it from source using Maven:
    `mvn clean install`

2) Execute the JAR via

    `java -jar target/zeebe-script-worker-{VERSION}.jar`

### Configuration

The connection can be changed by setting the environment variables `zeebe.client.broker.contactPoint` (default: `127.0.0.1:26500`).

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
