#!/bin/bash
set -xeo pipefail

set +e
pkill spire-server
set -e

rm -rf .data
nohup bin/spire-server run -config conf/spire-server.conf > logs/server.log &