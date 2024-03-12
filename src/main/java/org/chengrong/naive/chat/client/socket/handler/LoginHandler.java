package org.chengrong.naive.chat.client.socket.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.login.LoginResponse;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

public class LoginHandler extends SimpleChannelInboundHandler<LoginResponse> {
    private UIService uiService;

    public LoginHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponse msg) throws Exception {
        System.out.println("\r\n> msg handler ing ...");
        System.out.println("消息内容：" + JSON.toJSONString(msg));
        if (!msg.isSuccess()) {
            System.out.println("登录失败");
            return;
        }
        Platform.runLater(() -> {
            uiService.getLogin().doLoginSuccess();
            IChatMethod chat = uiService.getChat();
            chat.setUserInfo(msg.getUserId(), msg.getUserNickName(), msg.getUserHead());
        });
    }
}
