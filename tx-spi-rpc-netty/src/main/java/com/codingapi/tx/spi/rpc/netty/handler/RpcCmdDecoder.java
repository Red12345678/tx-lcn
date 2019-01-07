package com.codingapi.tx.spi.rpc.netty.handler;

import com.codingapi.tx.spi.rpc.MessageConstants;
import com.codingapi.tx.spi.rpc.netty.bean.NettyRpcCmd;
import com.codingapi.tx.spi.rpc.netty.bean.RpcContent;
import com.codingapi.tx.spi.rpc.netty.em.NettyType;
import com.codingapi.tx.spi.rpc.netty.impl.NettyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcCmdDecoder extends SimpleChannelInboundHandler<NettyRpcCmd> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyRpcCmd cmd) {
        String key = cmd.getKey();
        log.debug("cmd->{}", cmd);

        //心态数据包直接响应
        if (cmd.getMsg() != null && MessageConstants.ACTION_RPC_HEART.equals(cmd.getMsg().getAction())) {
            if (NettyContext.currentType().equals(NettyType.clent)) {
                ctx.writeAndFlush(cmd);
                return;
            } else {
                return;
            }
        }

        //需要响应的数据包
        if (!StringUtils.isEmpty(key)) {
            RpcContent rpcContent = cmd.loadRpcContent();
            if (rpcContent != null) {
                log.debug("got response message");
                rpcContent.setRes(cmd.getMsg());
                rpcContent.signal();
            } else {
                ctx.fireChannelRead(cmd);
            }
        } else {
            ctx.fireChannelRead(cmd);
        }
    }
}
