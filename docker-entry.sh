#!/bin/bash


# Functions for dependencies health-check
function fail {
  echo $1 >&2
  exit 1
}

function retry {
  local n=1
  local max=10
  local delay=3
  while true; do
    "$@" && break || {
      if [[ $n -lt $max ]]; then
        ((n++))
        echo "Command failed. Attempt $n/$max:"
        sleep $delay;
      else
        fail "The command has failed after $n attempts."
      fi
    }
  done
}


# Try connections to ElasticSearch, MongoDB, RabbitMQ and Redis.
retry curl -s --fail -s cl-elasticsearch:9200 > /dev/null
retry curl -s --fail -s cl-mongodb:27017 > /dev/null
retry curl -s --fail -s cl-rabbitmq:15672 > /dev/null
retry exec 6<>/dev/tcp/cl-redis/6379
sleep 2

# Execute the spring boot app.
java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar cl-lo.jar
