package com.fourfaith.server;

import com.fourfaith.registry.ServerRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author chenyuepeng
 * @create 2018-02-02 11:33
 * @since: 1.0.0
 * @desc 服务
 **/
public class NettyServer extends Thread {

    private static final Logger logger = Logger.getLogger(NettyServer.class);

    private String ip;
    private int port;// 服务监听端口
    private int socketTimeout;// socket超时时间（秒）
    private ServerRegistry serverRegistry;

    public NettyServer(String ip, int port, int socketTimeout, ServerRegistry serverRegistry) {
        this.ip = ip;
        this.port = port;
        this.socketTimeout = socketTimeout;
        this.serverRegistry = serverRegistry;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(socketTimeout, 0, 0, TimeUnit.SECONDS));
                    pipeline.addLast("handler", new NettyServerHandler());
                }
            });
            logger.info("Server binding port [" + port + "].");
            Channel ch = b.bind(port).sync().channel();

            if(serverRegistry != null) {
                serverRegistry.register(ip + ":" + port);
            }
            ch.closeFuture().sync();

        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
