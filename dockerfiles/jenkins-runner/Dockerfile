FROM jenkins/jenkinsfile-runner:adoptopenjdk-11-hotspot

# Install git, docker, sdkman
RUN apt-get update && \
  apt-get install -y wget git unzip zip apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
  curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg > /tmp/dkey; apt-key add /tmp/dkey && \
  add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
    $(lsb_release -cs) \
    stable" && \
  apt-get update && \
  apt-get -y install docker-ce &&  \
  rm -rf /var/lib/apt/lists/* &&  \
  rm /bin/sh && ln -s /bin/bash /bin/sh && curl -s "https://get.sdkman.io" | bash

# Install sbt
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install sbt