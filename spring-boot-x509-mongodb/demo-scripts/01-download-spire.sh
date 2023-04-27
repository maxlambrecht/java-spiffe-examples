#!/bin/bash
set -xeo pipefail

# download SPIRE binaries
wget https://github.com/spiffe/spire/releases/download/v1.6.3/spire-1.6.3-linux-x86_64-glibc.tar.gz

# extract binaries
tar -xzf spire-1.6.3-linux-x86_64-glibc.tar.gz

mv spire-1.6.3/bin/* bin/

rm -rf spire-1.6.3
rm spire-1.6.3-linux-x86_64-glibc.tar.gz
