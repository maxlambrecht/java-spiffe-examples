#!/bin/bash

bin/spire-server entry update \
  -entryID $1 \
	-spiffeID spiffe://example.org/myservice\
	-parentID spiffe://example.org/localNode \
	-selector unix:uid:$(id -u) \
	-ttl 3600 \
	-socketPath /tmp/spire-server/private/api.sock
