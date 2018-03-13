package com.fourfaith.comm;

/**
 * @author chenyuepeng
 * @create 2018-02-02 11:01
 * @since: 1.0.0
 * @desc 常量
 **/
public interface Constant {

    /**
     * 服务信息
     */
    String SERVER_IP = "server.ip";
    String SERVER_PORT = "server.port";
    String SERVER_SOCKET_TIMEOUT = "server.socket.timeout";

    /**
     * ZK信息
     */
    String ZK_ADDRESS = "zk.address";
    String ZK_SESSION_TIMEOUT = "zk.session.timeout";

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
