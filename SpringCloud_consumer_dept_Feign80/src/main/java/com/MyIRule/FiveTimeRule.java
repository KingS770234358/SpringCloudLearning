//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.MyIRule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FiveTimeRule extends AbstractLoadBalancerRule {
    /***
     * 每个服务访问5次 换下一个服务 总共三个服务
     * total=0 如果=5 换下一个服务节点
     * index=0 如果total=5 index+1
     */
    private int total = 0; // 当前服务已经被调用了多少次
    private int currentIndex = 0; // 当前是哪个服务在提供服务

    public FiveTimeRule() {
    }

    @SuppressWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"})
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        } else {
            Server server = null;

            while(server == null) {
                if (Thread.interrupted()) {
                    return null;
                }

                List<Server> upList = lb.getReachableServers();
                List<Server> allList = lb.getAllServers();
                int serverCount = allList.size();
                if (serverCount == 0) {
                    return null;
                }

//                int index = this.chooseRandomInt(serverCount);
//                server = (Server)upList.get(index);
                //===============================================================
                if(total<5){
                    System.out.println("total<5当前的服务ID:"+currentIndex);
                    server = upList.get(currentIndex);
                    total++;
                }else {
                    total=0;
                    currentIndex++;
                    if(currentIndex>=upList.size()){
                        currentIndex=0;
                    }
                    System.out.println("当前的服务ID:"+currentIndex);
                    server = upList.get(currentIndex); //从活着的服务中获取指定服务来进行操作
                    total++;
                }
                System.out.println(server);


                //===============================================================
                if (server == null) {
                    System.out.println("Server为空yield了");
                    Thread.yield();
                } else {
                    if (server.isAlive()) {

                        return server;
                    }

                    server = null;
                    System.out.println("Server为空yield了");
                    Thread.yield();
                }
            }


            return server;
        }
    }

    protected int chooseRandomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }

    public Server choose(Object key) {
        return this.choose(this.getLoadBalancer(), key);
    }

    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }
}
