#!/bin/sh

if [ -z ${JAVA_HOME+x} ]; then
  export JAVA_HOME=/usr/lib/jvm/grallvm-jdk-21
  echo $JAVA_HOME
  java -version
fi

echo "Start building component"
rm -rf ./target
mvn -B -U clean package install
echo "Building component completed"

docker-compose build

echo "You can now start the containers with command: docker-compose up"
