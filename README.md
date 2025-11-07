# Script Connector

[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)](https://github.com/camunda-community-hub/community/blob/main/extension-lifecycle.md#compatiblilty)

_This is a community project that provides a connector. It is not officially supported by Camunda. Everybody is invited to contribute!_
A connector to evaluate scripts (i.e. script tasks) that are not written in FEEL. Scripts are useful for prototyping, to do (simple) calculations, or creating/modifying variables.

## Usage

### Connector

The connector provides an [element template](./connector/element-templates/script-connector.json) that can be used to configure it.

### Script languages

Available script languages are by default:
* [javascript](https://www.graalvm.org/) (GraalVM JS) [Note: Consider using the [Camunda 8 JavaScript SDK for Node.js](https://docs.camunda.io/docs/apis-tools/node-js-sdk/) instead.]
* [groovy](http://groovy-lang.org/)
* [mustache](http://mustache.github.io/mustache.5.html)
* [kotlin](https://kotlinlang.org/)

To register new script languages, you can use the `ScriptEngineFactory` to register any JSR-223 compliant script engine.

If you want to provide a non-compliant implementation, you can use the [`ScriptEvaluatorExtension`](./connector/src/main/java/io/camunda/community/connector/script/spi/ScriptEvaluatorExtension.java) SPI.

To register custom file extensions, you can use the [`LanguageProviderExtension`](./connector/src/main/java/io/camunda/community/connector/script/spi/LanguageProviderExtension.java) SPI.

### Legacy

>The legacy implementation is still in place to support older implementations. Please use the new connector!

The legacy connector provides compatibility with the previous implementation `zeebe-script-worker`.

>The context does not offer access to `job` or `zeebeClient` anymore.

Example BPMN with service task:

```xml
<bpmn:serviceTask id="scripting" name="Evaluate the Script">
  <bpmn:extensionElements>
    <zeebe:taskDefinition type="script" />
    <zeebe:taskHeaders>
      <zeebe:header key="language" value="javascript" />
      <zeebe:header key="script" value="a + b" />
      <zeebe:header key="resultVariable" value="result" />
    </zeebe:taskHeaders>
  </bpmn:extensionElements>
</bpmn:serviceTask>
```

* the worker is registered for the type `script`
* required custom headers:
  * `language` - the name of the script language
  * `script` - the script to evaluate
  * `resultVariable` - the result of the evaluation is passed to this variable

## Install

### Docker

For a local setup, the repository contains a [docker-compose file](docker/docker-compose.yml). It starts a Zeebe broker and both (standalone and bundled) containers.

```
mvn clean package
cd docker
docker-compose up
```

#### Standalone Runtime

The docker image for the connector runtime is published as GitHub package.

```
docker pull ghcr.io/camunda-community-hub/script-connector/runtime:latest
```

Configure the connection to the Zeebe broker by following the [official documentation of the camunda spring boot starter](https://docs.camunda.io/docs/apis-tools/camunda-spring-boot-starter/getting-started/).

The docker-compose file shows an example how this works.

#### Bundled Runtime

To run the connector inside the bundle, you can use the shaded jar.

The docker-compose file shows an example how this works.

### Manual

#### Standalone Runtime

1. Download the runtime jar `script-connector-runtime-{VERSION}.jar`
2. Start the connector runtime `java -jar script-connector-runtime-{VERSION}.jar`

#### Bundled Runtime

1. Download the shaded connector jar  `script-connector-{VERSION}-shaded.jar`
2. Copy it to your connector runtime.

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
