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
Some examples for common use cases:

### Single Value

JavaScript:
```javascript
'url?id=' + id
```

Result:
```json
{"result": "url?id=123"}
```

### Complex Value

JavaScript:
```javascript
result = {};
result.orderId = id;
result.price = price * 1.20;
result;
```
Groovy:
```groovy
[orderId:id,
 price:price * 1.20 ]
```

FEEL:
```
{orderId:id,
 price:price * 1.20 }
```

Result:
```json
{"result": {
 "orderId": 123,
 "price": 120.0 }}
```

### Aggregation

Jobs' payload:
```json
{"order": {
 "id": 123,
 "items": [
  {"id":"i1", "price":5.99},
  {"id":"i2", "price":29.99},
  {"id":"i3", "price":10.00}
 ]}}
```

FEEL:
```
sum(order.items.price)                                    
// result: 45.98

some item in order.items satisfies item.price > 20.00     
// result: true

order.items[price >= 10.00]                               
// result: [{"id":"i2", "price":29.99}, {"id":"i3", "price":10.00}]
```

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
