package com.fourfaith.registry;

import com.fourfaith.comm.Constant;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenyuepeng
 * @create 2018-02-02 14:39
 * @since: 1.0.0
 * @desc 服务注册
 **/
public class ServerRegistry {

    private static final Logger logger = Logger.getLogger(ServerRegistry.class);
    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;
    private int zkSessionTimeout;

    public ServerRegistry(String registryAddress, int zkSessionTimeout) {
        this.registryAddress = registryAddress;
        this.zkSessionTimeout = zkSessionTimeout;
    }

    //注册到zk中，其中data为服务端的 ip:port
    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                AddRootNode(zk); // Add root node if not exist
                createNode(zk, data);
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, zkSessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (InterruptedException ex){
            logger.error(ex.getMessage(), ex);
        }
        return zk;
    }

    private void AddRootNode(ZooKeeper zk){
        try {
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("path = " + path + ",data = " + data);
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        }
        catch (InterruptedException ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
