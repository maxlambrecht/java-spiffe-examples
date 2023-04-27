#!/bin/bash
set -xeo pipefail

set +e

pkill spire-server
pkill spire-agent
docker container rm -f mongodb

java_pid=$(pgrep java)
if [[ -n "$java_pid" ]]; then
  kill "$java_pid"
  echo "No Java app processes found"
fi

set -e