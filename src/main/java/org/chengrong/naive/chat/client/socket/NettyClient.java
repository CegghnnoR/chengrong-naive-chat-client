package org.chengrong.naive.chat.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.client.infrastructure.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.Callable;

public class NettyClient implements Callable<Channel> {
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String inetHost = "127.0.0.1";
    private int inetPort = 7397;

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    private UIService uiService;

    public NettyClient(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    public Channel call() throws Exception {
        ChannelFuture channelFuture = null;
        try {
            Bootstrap b = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, true)
                    .handler(new MyChannelInitializer(uiService));
            channelFuture = b.connect(inetHost, inetPort).syncUninterruptibly();
            channel = channelFuture.channel();
            BeanUtil.addBean("channel", channel);
        } catch (Exception e) {
            logger.error("socket client start error", e.getMessage());
        } finally {
            if (channelFuture != null && channelFuture.isSuccess()) {
                logger.info("socket client start done. ");
            } else {
                logger.error("socket client start error. ");
            }
        }
        return channel;
    }

    public void destroy() {
        if (channel == null) {
            return;
        }
        channel.close();
        workerGroup.shutdownGracefully();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public Channel channel() {
        return channel;
    }
}
