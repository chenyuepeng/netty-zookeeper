package com.fourfaith;

import com.fourfaith.client.ServiceDiscovery;
import com.fourfaith.comm.Constant;
import com.fourfaith.comm.PropertyConfig;
import org.apache.log4j.Logger;

/**
 * @author chenyuepeng
 * @create 2018-02-05 10:21
 * @since: 1.0.0
 * @desc 客户端主函数
 **/
public class ClientMain {

    private static final Logger logger = Logger.getLogger(ClientMain.class);
    private static String zkAddress = PropertyConfig.getProperty(Constant.ZK_ADDRESS);
    private static int zkSessionTimeout = Integer.parseInt(PropertyConfig.getProperty(Constant.ZK_SESSION_TIMEOUT));

    public static void main(String[] args) throws InterruptedException {
        //find service from zookeeper
        ServiceDiscovery sd = new ServiceDiscovery(zkAddress, zkSessionTimeout);



        String serverIp = sd.discover();
        logger.info("get server addr: " + serverIp);
    }
}
