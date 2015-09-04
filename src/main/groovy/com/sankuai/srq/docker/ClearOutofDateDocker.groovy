package com.sankuai.srq.docker

import de.gesellix.docker.client.DockerClient;
import de.gesellix.docker.client.DockerClientImpl
import de.gesellix.docker.client.DockerResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClearOutofDateDocker {
	def static Logger log = LoggerFactory.getLogger("ClearOutofDateDocker");
	def static void main(String[] args){
		
		DockerClient dClient = new DockerClientImpl(dockerHost:'http://172.27.2.94:4243',)
		DockerResponse response = dClient.ps(query:[all: true, size: true])
		log.info "containers to be checked:${response.content.size()}"
		if(response.status.success==true){
			response.content.each { container->
				long nowT=System.currentTimeMillis()/1000
				if(nowT-container.Created>=60*60*8){
					//12个小时以前启动的docker实例
					log.info "[TODO][stop and remove container]${container.Names},${container.Status}"
					log.info "stop container......"
					response = dClient.stop(container.Id)
					log.info response.status.success.toString()
					if(response.status.success==true || response.status.code==304){
						//304:资源未更改. 表示container已经被停止了
						log.info "remove contaner......"
						response = dClient.rm(container.Id)
						log.info response.status.success.toString()
						if(response.status.success==true){
							log.info "[success][remove container]${container.Names},${container.Status}"
						}else{
							log.info response.toString()
						}
					}else{
						log.info response.toString()
					}
				}else{
					log.info "[Don't stop this container]${container.Names},${container.Status}"
				}
			}
		}else{
			log.info "[fail]docker ps -a"
			log.info response.toString()
		}
	}
}
