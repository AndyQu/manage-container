package com.sankuai.srq.docker

import de.gesellix.docker.client.DockerClient;
import de.gesellix.docker.client.DockerClientImpl

class ClearOutofDateDocker {
	def static void main(String[] args){
		DockerClient dClient = new DockerClientImpl(dockerHost:'http://127.0.0.1:4243',)
		dClient.ps(query:[all: true, size: false])
	}
}
