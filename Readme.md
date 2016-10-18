# Java Callout Unit Testing with TestNG, EasyTest and JMockit

This directory contains Java source code for a callout which executes regular expression matcher. The purpose of this repo is to demo unit testing Java Callouts with TestNG, Easytest and JMockit.


- [Java source](callout) - Java code, as well as instructions for how to build the Java code.
- [apiproxy](apiproxy) - an example API Proxy for Apigee Edge that shows how to use the sample Java callout
- [bin](bin) - files to invoke a sample test against the deployed proxy


## Dependencies
There are two dependencies that are required to execute this project.  

 - Apigee Edge expressions v1.0
 - Apigee Edge message-flow v1.0

### How do I get the dependencies?

Unfortunately, these jar files are not in the Maven central repository.  However, they are included in the `lib` directory.  You must install these into your local maven repository by following the steps below.  Once you install these jar files they you can run `mvn package` or `mvn test` to run the TestNG test scripts.

1. `cd lib`
2. Execute the following maven command to install the expressions-1.0.0.jar file.
```
mvn install:install-file \
 -Dfile=expressions-1.0.0.jar \
 -DgroupId=com.apigee.edge \
 -DartifactId=expressions \
 -Dversion=1.0.0 \
 -Dpackaging=jar \
 -DgeneratePom=true
```

3. Execute the following maven command to install the message-flow-1.0.0.jar file.
```
mvn install:install-file \
 -Dfile=message-flow-1.0.0.jar \
 -DgroupId=com.apigee.edge \
 -DartifactId=message-flow \
 -Dversion=1.0.0 \
 -Dpackaging=jar \
 -DgeneratePom=true
```


You can also download the jar files from here.
https://github.com/apigee/api-platform-samples/tree/master/doc-samples/java-cookbook/lib


## Deploy the Sample Proxy To Edge

1. cd to the `apigee-javacallout-testng` directory

2. Execute the following maven command.  This will build the regex-callout.jar files and copy it into    the `apiproxy/resources/java` directory.  It will execute the test TestNG tests and then deploy the proxy to your org.  
  ```
  mvn install -Penv -Dusername=orgadmin@email.com -Dpassword=orgadminpwd -Dorg=orgname
  ```
### Build results
```
[INFO] ------------------------------------------------------------------------
[INFO] Building java-callout-test 1.0
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:copy-resources (default) @ java-callout-test ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 9 resources
[INFO]
[INFO] --- apigee-edge-maven-plugin:1.1.0:configure (configure-bundle) @ java-callout-test ---
[INFO] No config.json found. Skipping package configuration.
[INFO]
...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] RegexCallout ....................................... SUCCESS [  3.293 s]
[INFO] java-callout-test .................................. SUCCESS [  0.823 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.238 s
[INFO] Finished at: 2016-10-17T20:21:06-05:00
[INFO] Final Memory: 16M/437M
[INFO] ------------------------------------------------------------------------
...
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO]
[INFO] RegexCallout
[INFO] java-callout-test
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building RegexCallout 1.0.0
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:3.0.0:copy-resources (copy-files-on-build) @ regex-callout ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] ignoreDelta true
[INFO] Copying 1 resource
[INFO] Copying file regex-callout.jar
[INFO]
[INFO] --- maven-resources-plugin:3.0.0:resources (default-resources) @ regex-callout ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory ...
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ regex-callout ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-resources-plugin:3.0.0:testResources (default-testResources) @ regex-callout ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory ...
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ regex-callout ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:2.19.1:test (default-test) @ regex-callout ---

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running TestSuite
objc[34658]: Class JavaLaunchHelper is implemented in both ...
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.079 sec - in TestSuite

Results :

Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

[INFO]
Deployed revision is: 7
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] RegexCallout ....................................... SUCCESS [  3.223 s]
[INFO] java-callout-test .................................. SUCCESS [ 10.747 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 14.070 s
[INFO] Finished at: 2016-10-17T20:21:29-05:00
[INFO] Final Memory: 22M/365M
[INFO] ------------------------------------------------------------------------
```

## Invoke Sample Curl commands
1. Update the following fields in the `setenv.sh` file located in the `bin` directory.
```
org="orgname"
username="orgadmin@email.com"
url="https://api.enterprise.apigee.com"
env="test"
api_domain="apigee.net"
```

2. Once the proxy is deployed you can execute the following command.
```
cd bin

./invoke.sh
```

3. You should receive the following results after you execute `invoke.sh`.
```
Using org and environment configured in /setup/setenv.sh
HTTP/1.1 400 Bad Request
Date: Tue, 18 Oct 2016 02:32:52 GMT
Content-Type: application/json
Content-Length: 85
Connection: keep-alive
Server: Apigee Router

{
  "error" : {
    "code" : 400,
    "message" : "Threat detected in request"
  }
}
HTTP/1.1 400 Bad Request
Date: Tue, 18 Oct 2016 02:32:53 GMT
Content-Type: application/json
Content-Length: 85
Connection: keep-alive
Server: Apigee Router

{
  "error" : {
    "code" : 400,
    "message" : "Threat detected in request"
  }
}
```
