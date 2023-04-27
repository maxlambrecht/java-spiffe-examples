#!/bin/bash
set -xeo pipefail

join_token_output=$(bin/spire-server token generate -spiffeID spiffe://example.org/localNode -socketPath /tmp/spire-server/private/api.sock)
echo $join_token_output

regex='Token: ([a-z0-9-]*)'
if [[ $join_token_output =~ $regex ]]
then
        join_token="${BASH_REMATCH[1]}"
        echo $join_token
else
        echo "Unexpected output from \"spire-server token generate\": ${join_token_output}"
	exit 1
fi

set +e
pkill spire-agent
set -e

sleep 2

nohup bin/spire-agent run -config conf/spire-agent.conf -joinToken ${join_token} > logs/agent.log &