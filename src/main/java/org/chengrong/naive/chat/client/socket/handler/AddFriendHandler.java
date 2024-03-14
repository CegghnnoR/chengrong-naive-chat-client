package org.chengrong.naive.chat.client.socket.handler;

import io.netty.channel.Channel;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.client.socket.MyBizHandler;
import org.chengrong.naive.chat.protocol.friend.AddFriendResponse;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

public class AddFriendHandler extends MyBizHandler<AddFriendResponse> {
    public AddFriendHandler(UIService uiService) {
        super(uiService);
    }

    @Override
    public void channelRead(Channel ctx, AddFriendResponse msg) {
        IChatMethod chat = uiService.getChat();
        Platform.runLater(() -> {
            chat.addFriendUser(true, msg.getFriendId(),
                    msg.getFriendNickName(), msg.getFriendHead());
        });
    }
}
