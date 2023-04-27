#!/bin/bash
set -xeo pipefail

bin/spire-server entry create \
	-spiffeID spiffe://example.org/mongoServer \
	-parentID spiffe://example.org/localNode \
	-selector unix:uid:$(id -u mongodb) \
	-dns localhost-0 \
	-ttl 3000000 \
	-socketPath /tmp/spire-server/private/api.sock

bin/spire-server entry create \
	-spiffeID spiffe://example.org/myservice\
	-parentID spiffe://example.org/localNode \
	-selector unix:uid:$(id -u) \
	-dns dbuser \
	-ttl 3600 \
	-socketPath /tmp/spire-server/private/api.sock