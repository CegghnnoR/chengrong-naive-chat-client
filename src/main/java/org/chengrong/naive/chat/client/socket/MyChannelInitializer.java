package org.chengrong.naive.chat.client.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.client.socket.handler.*;
import org.chengrong.naive.chat.codec.ObjDecoder;
import org.chengrong.naive.chat.codec.ObjEncoder;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private UIService uiService;

    public MyChannelInitializer(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 对象传输处理[解码]
        ch.pipeline().addLast(new ObjDecoder());
        // 添加自定义的接收数据实现方法
        ch.pipeline().addLast(new LoginHandler(uiService));
        ch.pipeline().addLast(new SearchFriendHandler(uiService));
        ch.pipeline().addLast(new AddFriendHandler(uiService));
        ch.pipeline().addLast(new TalkNoticeHandler(uiService));
        ch.pipeline().addLast(new MsgHandler(uiService));
        ch.pipeline().addLast(new MsgGroupHandler(uiService));
        // 对象传输处理[编码]
        ch.pipeline().addLast(new ObjEncoder());
    }
}
