package com.fourfaith;

import com.fourfaith.comm.Constant;
import com.fourfaith.comm.PropertyConfig;
import com.fourfaith.registry.ServerRegistry;
import com.fourfaith.server.NettyServer;
import org.apache.log4j.Logger;


/**
 * @author chenyuepeng
 * @create 2018-02-02 14:02
 * @since: 1.0.0
 * @desc 启动类
 **/
public class ServerContextLoader {

    private static final Logger logger = Logger.getLogger(ServerContextLoader.class);
    private static String ip = PropertyConfig.getProperty(Constant.SERVER_IP);
    private static int port = Integer.parseInt(PropertyConfig.getProperty(Constant.SERVER_PORT));
    private static int socketTimeout = Integer.parseInt(PropertyConfig.getProperty(Constant.SERVER_SOCKET_TIMEOUT));
    private static String zkAddress = PropertyConfig.getProperty(Constant.ZK_ADDRESS);
    private static int zkSessionTimeout = Integer.parseInt(PropertyConfig.getProperty(Constant.ZK_SESSION_TIMEOUT));

    public static void main(String[] args) throws Exception {
        ServerRegistry serverRegistry = new ServerRegistry(zkAddress, zkSessionTimeout);
        // 服务加载
        new NettyServer(ip, port, socketTimeout * 60, serverRegistry).start();
    }
}
