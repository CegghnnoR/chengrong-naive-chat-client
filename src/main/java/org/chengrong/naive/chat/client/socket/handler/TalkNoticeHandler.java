package org.chengrong.naive.chat.client.socket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.talk.TalkNoticeResponse;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

public class TalkNoticeHandler extends SimpleChannelInboundHandler<TalkNoticeResponse> {
    private UIService uiService;

    public TalkNoticeHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TalkNoticeResponse msg) throws Exception {
        IChatMethod chat = uiService.getChat();
        Platform.runLater(() -> {
            chat.addTalkBox(-1, 0, msg.getTalkId(),
                    msg.getTalkName(), msg.getTalkHead(), msg.getTalkSketch(),
                    msg.getTalkDate(), false);
        });
    }
}
