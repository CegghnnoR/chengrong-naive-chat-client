package org.chengrong.naive.chat.client.socket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.msg.MsgResponse;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

public class MsgHandler extends SimpleChannelInboundHandler<MsgResponse> {
    private UIService uiService;

    public MsgHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgResponse msg) throws Exception {
        IChatMethod chat = uiService.getChat();
        Platform.runLater(() -> {
            chat.addTalkMsgUserLeft(msg.getFriendId(),
                    msg.getMsgText(), msg.getMsgType(),
                    msg.getMsgDate(), true, false, true);
        });
    }
}
