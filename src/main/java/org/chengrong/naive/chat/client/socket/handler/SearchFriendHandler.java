package org.chengrong.naive.chat.client.socket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.friend.SearchFriendResponse;
import org.chengrong.naive.chat.protocol.friend.dto.UserDto;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

import java.util.List;

public class SearchFriendHandler extends SimpleChannelInboundHandler<SearchFriendResponse> {
    private UIService uiService;

    public SearchFriendHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SearchFriendResponse msg) throws Exception {
        List<UserDto> list = msg.getList();
        if (list == null || list.isEmpty()) {
            return;
        }
        IChatMethod chat = uiService.getChat();
        Platform.runLater(() -> {
            for (UserDto user : list) {
                chat.addLuckFriend(user.getUserId(), user.getUserNickName(), user.getUserHead(), user.getStatus());
            }
        });
    }
}
