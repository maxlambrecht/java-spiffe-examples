#!/bin/bash
set -xeo pipefail

sudo apt update && sudo apt -y full-upgrade -y
sudo apt -y install software-properties-common wget \
        make git-core openjdk-17-jdk

# install docker
sudo apt install apt-transport-https ca-certificates -y
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io -y
sudo chmod 777 /var/run/docker.sock

# install go1.20.3 (not available in apt)
wget https://storage.googleapis.com/golang/go1.20.3.linux-amd64.tar.gz
sudo tar -C /usr/local -xzvf go1.20.3.linux-amd64.tar.gz
echo "export PATH=$PATH:/usr/local/go/bin" >> ~/.bashrc
source ~/.bashrc
rm go1.20.3.linux-amd64.tar.gz

# prepare folders
rm -rf build
rm -rf bin
rm -rf logs
mkdir build
mkdir bin
mkdir logs

# create user for mongodb
sudo useradd -r -s /bin/false mongodb
sudo sh -c 'echo "mongodb ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/mongodb'

chmod 777 .
chmod 777 logs
chmod 777 conf
