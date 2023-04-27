#!/bin/bash

export SPIFFE_ENDPOINT_SOCKET=unix:///tmp/spire-agent/public/api.sock
nohup java -jar bin/frontend-0.1.jar > logs/frontend.log &
