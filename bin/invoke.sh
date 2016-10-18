#!/bin/bash

echo Using org and environment configured in /setup/setenv.sh

source ./setenv.sh

curl -X POST -H "Content-type: application/json" "https://$org-$env.$api_domain/v1/javacallout/regex/protect" -d "<script>test</script>" -i

curl -X POST -H "Content-type: application/json" -H "x-myhack: <script>danger.js</script>" "https://$org-$env.$api_domain/v1/javacallout/regex/protect" -d "test" -i
