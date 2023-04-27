#!/bin/bash

export SPIFFE_ENDPOINT_SOCKET=unix:///tmp/spire-agent/public/api.sock
nohup java -Djava.security.properties=conf/java.security -jar bin/backend-0.1.jar > logs/backeng.log &
