#!/bin/bash
set -xeo pipefail

root_dir=$(pwd)
cd build

rm -rf spire-plugins
git clone https://github.com/maxlambrecht/spire-plugins
cd spire-plugins

git checkout dev/credential-composer-plugins
cd credential-composer-plugins/withdomaincomponent
make build

cd "$root_dir"

cp build/spire-plugins/credential-composer-plugins/withdomaincomponent/credential-composer-add-dc bin/


