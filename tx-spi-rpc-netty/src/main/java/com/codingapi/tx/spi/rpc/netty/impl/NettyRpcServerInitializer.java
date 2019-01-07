package com.codingapi.tx.spi.rpc.netty.impl;

import com.codingapi.tx.spi.rpc.RpcServerInitializer;
import com.codingapi.tx.spi.rpc.dto.ManagerProperties;
import com.codingapi.tx.spi.rpc.netty.em.NettyType;
import com.codingapi.tx.spi.rpc.netty.handler.NettyRpcServerHandlerInitHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@Service
@Slf4j
public class NettyRpcServerInitializer implements RpcServerInitializer, DisposableBean {

    @Autowired
    private NettyRpcServerHandlerInitHandler nettyRpcServerHandlerInitHandler;

    private EventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;


    @Override
    public void init(ManagerProperties managerProperties) {
        NettyContext.type = NettyType.server;
        NettyContext.params = managerProperties;

        nettyRpcServerHandlerInitHandler.setManagerProperties(managerProperties);

        int port = managerProperties.getRpcPort();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyRpcServerHandlerInitHandler);

            // Start the server.
            b.bind(port);
            log.debug("Socket started on rpcPort(s):{}(socket)",port);

        } catch (Exception e) {
            // Shut down all event loops to terminate all threads.
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.debug("server was down.");
    }


}
