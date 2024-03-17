package org.chengrong.naive.chat.client.socket.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.msg.MsgGroupResponse;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

public class MsgGroupHandler extends SimpleChannelInboundHandler<MsgGroupResponse> {
    UIService uiService;

    public MsgGroupHandler(UIService uiService) {
        this.uiService = uiService;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgGroupResponse msg) throws Exception {
        IChatMethod chat = uiService.getChat();
        Platform.runLater(() -> {
            System.out.println(msg.getMsgType());
            chat.addTalkMsgGroupLeft(msg.getTalkId(), msg.getUserId(),
                    msg.getUserNickName(), msg.getUserHead(), msg.getMsg(),
                    msg.getMsgType(), msg.getMsgDate(), true, false, true);
        });
    }
}
