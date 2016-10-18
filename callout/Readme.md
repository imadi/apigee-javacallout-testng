# Java Callout Unit Testing with TestNG, Easytesting and JMockit

This directory contains the Java source code and Java jars required to
compile a Java callout for Apigee Edge that does regular expression pattern matching for threat protection.

The API Proxy subdirectory, which is a sibling to this one, includes the pre-built JAR file. Therefore you do not need to build this Java code in order to deploy the sample proxy.

However, you may wish to modify this code for your own purposes. In that case, you will modify the Java code, re-build, then copy that JAR into the appropriate apiproxy/resources/java directory for the API Proxy.  

## Summary
This project demonstrates how to use TestNG, Easytesting and JMockit. The attached Java Callout will use the regular expression included in the `regex` property and evaluate the `request.content` and the request headers to determine if it finds that pattern.  

* If there are no headers, then it will not search for the pattern there.
* If the `toMatch` property is set, then it will check for threats in the variable included in that property.
* If the `toMatch` property is set, but it evaluates to null or blank, then it will abort and raise an error..
* If the `toMatch` property is missing, then it will abort and raise and error.

## What is TestNG?
[TestNG](http://testng.org/doc/documentation-main.html) is a unit testing tool that I used to unit test Java Callouts so that I can debug and troubleshoot it on my local machine.

## What is Easytest?
[Easytesting](https://github.com/easytesting) is a tool to test private methods. Of course you can test all of your private methods through your public interface, but if your private methods have sufficient complexity then you should should test them.  I recently worked on a Java Callout with several private methods that created multiple threads and threw Runtime exceptions. Therefore, I thought it was necessary to unit test these methods.  


## What is JMockit?
[JMockit](http://jmockit.org/tutorial/Introduction.html) is a tool that can be used to mock dependent objects of the class that you are testing.  This is required to test Java Callouts because we need to mock the MessageContext and the ExecutionContext classes.  

### Flow Variables Created by this Callout

#### On Success
If the policy executes successfully, then the following flow variables are set.

1. `flw.apigee.status` - Either success or unsuccessful.
2. `flw.apigee.patternFound` - the pattern that was found.
3. `flw.apigee.match` - the matching string
4. `flw.apigee.patternNotFound` - populated if the callout does not result in an error and it does not find a pattern.

#### On Failure
If an error occurs, the an error is raised and the flow is sent to the error flow. Also, the following items are set:
1. `x-Exception-Class` - a header with the Exception class name. (i.e `java.lang.IllegalStateException`)
2. `flw.apigee.status` - unsuccessful.


### Configuring the Callout Policy:

See example below:

```xml
<JavaCallout name='Java.Regex'>
  <Properties>
    <!-- JS injection patterns -->
    <Property name="regex">(?i)(&lt;\s*script\b[^>]*>[^&lt;]+&lt;\s*.+\s*[s][c][r][i][p][t]\s*>)</Property>
    <Property name="matchHeaders">true</Property>
    <Property name="toMatch">request.content</Property>
    <Property name="username">mysample@email.com</Property>

  </Properties>
  <ClassName>com.apigee.callout.JavaCallout</ClassName>
  <ResourceURL>java://regex-callout.jar</ResourceURL>
</JavaCallout>
```

## Using the Jar

You do not need to build the JAR in order to use it. The jar is located in
`apiproxy/resources/java` directory.
To use it:

1. Include the Java callout policy in your
   apiproxy/policies directory. The configuration should look like
   this:
    ```xml
  <JavaCallout name='Java.Regex'>
      <Properties>
        ...
      </Properties>
      <ClassName>com.apigee.callout.JavaCallout</ClassName>
      <ResourceURL>java://regex-callout.jar</ResourceURL>
</JavaCallout>
   ```

2. Deploy your API Proxy

For some examples of how to configure the callout, see the related api proxy bundle.


## How do I get the dependencies?
You must have the required dependencies in your local maven repository.

 - Apigee Edge expressions v1.0
 - Apigee Edge message-flow v1.0

Follow the instructions in the [Dependencies section.](apigee-javacallout-testng)

## Building the Jar

To build the binary JAR yourself, follow
these instructions.

1. cd to the `callout` directory.

2. Build the binary with [Apache maven](https://maven.apache.org/). You need to first install it, and then you can execute the following line in your terminal:  
   ```
   mvn clean package
   ```

3. Maven will copy all the required jar files to the `apiproxy/resources/java` directory.
   The `regex-callout.jar` file will also be located in the `callout/src/target/` directory.


## Skip Tests
If you don't want to execute the tests then execute the following line. However,
if you change the source code you should execute the tests to make you didn't
break the existing functionality.
```
mvn package -DskipTests
```

## Execute TestNG tests
```
mvn test
```
