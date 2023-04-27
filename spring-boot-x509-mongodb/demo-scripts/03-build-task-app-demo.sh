#!/bin/bash
set -xeo pipefail

root_dir=$(pwd)

cd build

rm -rf java-spiffe-examples
git clone https://github.com/maxlambrecht/java-spiffe-examples
cd java-spiffe-examples/spring-boot-x509-mongodb
./gradlew clean build

cd "$root_dir"

cp build/java-spiffe-examples/spring-boot-x509-mongodb/backend/build/libs/backend-0.1.jar bin/
cp build/java-spiffe-examples/spring-boot-x509-mongodb/frontend/build/libs/frontend-0.1.jar bin/


