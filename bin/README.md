# Bin directory
This directory contains two files
1. `setenv.sh` - configure environment variables, which the `invoke.sh` files uses.
2. `invoke.sh` - can be used to invoke the sample curl requests after the proxy is deployed to your org.


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
