package com.sankuai.srq.docker

import de.gesellix.docker.client.DockerClient;
import de.gesellix.docker.client.DockerClientImpl
import de.gesellix.docker.client.DockerResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClearOutofDateDocker {
	def static Logger logger = LoggerFactory.getLogger("ClearOutofDateDocker");
	def static void main(String[] args){
		
		DockerClient dClient = new DockerClientImpl(dockerHost:'http://172.27.2.94:4243',)
		DockerResponse response = dClient.ps(query:[all: true, size: true])
		logger.info "containers to be checked:${response.content.size()}"
		if(response.status.success==true){
			response.content.each { container->
				long nowT=System.currentTimeMillis()/1000
				if(nowT-container.Created>=60*60*8){
					//12个小时以前启动的docker实例
					logger.info "[TODO][stop and remove container]${container.Names},${container.Status}"
					logger.info "stop container......"
					response = dClient.stop(container.Id)
					logger.info response.status.success.toString()
					if(response.status.success==true || response.status.code==304){
						//304:资源未更改. 表示container已经被停止了
						logger.info "remove contaner......"
						response = dClient.rm(container.Id)
						logger.info response.status.success.toString()
						if(response.status.success==true){
							logger.info "[success][remove container]${container.Names},${container.Status}"
						}else{
							logger.error response.toString()
						}
                        container.Names.each {
                            name->
                                def result = new File("/docker-deploy/${name}").deleteDir()
                                if(result==true){
                                    logger.info "removed folder:/docker-deploy/${name}"
                                }else{
                                    logger.error "failed removed folder:/docker-deploy/${name}"
                                }
                        }
                        
					}else{
						logger.error response.toString()
					}
				}else{
					logger.info "[Don't stop this container]${container.Names},${container.Status}"
				}
			}
		}else{
			logger.info "[fail]docker ps -a"
			logger.info response.toString()
		}
	}
}
