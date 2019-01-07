package com.codingapi.tx.spi.rpc.netty.handler;

import com.codingapi.tx.commons.util.RandomUtils;
import com.codingapi.tx.spi.rpc.MessageConstants;
import com.codingapi.tx.spi.rpc.dto.MessageDto;
import com.codingapi.tx.spi.rpc.dto.RpcCmd;
import com.codingapi.tx.spi.rpc.netty.SocketManager;
import com.codingapi.tx.spi.rpc.netty.bean.NettyRpcCmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@ChannelHandler.Sharable
@Slf4j
public class SocketManagerInitHandler extends ChannelInboundHandlerAdapter {

    private RpcCmd heartCmd;

    public SocketManagerInitHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_RPC_HEART);
        heartCmd = new NettyRpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(RandomUtils.randomKey());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        SocketManager.getInstance().addChannel(ctx.channel());
        log.info("Connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        SocketManager.getInstance().removeChannel(ctx.channel());
        log.error("Disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(heartCmd);
            }
        }
    }

}
