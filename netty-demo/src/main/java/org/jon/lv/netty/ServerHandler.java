package org.jon.lv.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;

/**
 * Package: org.jon.lv.netty.ServerHandler
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/12 13:43
 * Version: V1.0.0
 */
public class ServerHandler extends SimpleChannelInboundHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        System.out.println("----------------server start read---------------");
        ByteBuf buf = (ByteBuf) o;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        String body = new String(bytes, "UTF-8");

        System.out.println("----------receive over ----------" + body);
        String rtnMsg = LocalDateTime.now() + "---" + body;
        ByteBuf buf1 = Unpooled.copiedBuffer(rtnMsg.getBytes());
        ctx.write(buf1);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
