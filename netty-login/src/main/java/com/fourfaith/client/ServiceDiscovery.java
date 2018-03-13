package com.fourfaith.client;

import com.fourfaith.comm.Constant;
import io.netty.util.internal.ThreadLocalRandom;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenyuepeng
 * @create 2018-02-05 10:01
 * @since: 1.0.0
 * @desc 客户端连接
 **/
public class ServiceDiscovery {

    private static final Logger logger = Logger.getLogger(ServiceDiscovery.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile List<String> dataList = new ArrayList<String>();
    private String registryAddress;
    private int zkSessionTimeout;


    public ServiceDiscovery(String registryAddress, int zkSessionTimeout) {
        this.registryAddress = registryAddress;
        this.zkSessionTimeout = zkSessionTimeout;
        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
            } else {
                //随机获取其中的一个
                //负载均衡策略可以自定义
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
            }
        }
        return data;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            //format host1:port1,host2:port2,host3:port3
            zk = new ZooKeeper(registryAddress, zkSessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    //zookeeper处于同步连通的状态时
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException ie) {
            logger.error(ie.getMessage(), ie);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<String>();
            if(nodeList != null && nodeList.size() > 0) {
                logger.info("nodeList.size = " + nodeList.size());
                for (String node : nodeList) {
                    byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                    dataList.add(new String(bytes));
                }
            }
            this.dataList = dataList;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
