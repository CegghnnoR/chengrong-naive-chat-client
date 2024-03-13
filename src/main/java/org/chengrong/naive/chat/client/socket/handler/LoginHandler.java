package org.chengrong.naive.chat.client.socket.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.protocol.login.LoginResponse;
import org.chengrong.naive.chat.protocol.login.dto.ChatRecordDto;
import org.chengrong.naive.chat.protocol.login.dto.ChatTalkDto;
import org.chengrong.naive.chat.protocol.login.dto.GroupsDto;
import org.chengrong.naive.chat.protocol.login.dto.UserFriendDto;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;

import java.util.List;

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
            // 设置用户信息
            chat.setUserInfo(msg.getUserId(), msg.getUserNickName(), msg.getUserHead());
            // 对话框
            List<ChatTalkDto> chatTalkList = msg.getChatTalkList();
            if (chatTalkList != null) {
                chatTalkList.forEach(talk -> {
                    chat.addTalkBox(0, talk.getTalkType(), talk.getTalkId(),
                            talk.getTalkName(), talk.getTalkHead(), talk.getTalkSketch(),
                            talk.getTalkDate(), true);
                    switch (talk.getTalkType()) {
                        case 0:
                            // 好友
                            List<ChatRecordDto> userRecordList = talk.getChatRecordList();
                            if (userRecordList == null || userRecordList.isEmpty()) {
                                return;
                            }
                            for (int i = userRecordList.size() - 1; i >= 0; --i) {
                                ChatRecordDto chatRecord = userRecordList.get(i);
                                // 自己的消息
                                if (chatRecord.getMsgType() == 0) {
                                    chat.addTalkMsgRight(chatRecord.getTalkId(),
                                            chatRecord.getMsgContent(), chatRecord.getMsgType(),
                                            chatRecord.getMsgDate(), true, false,
                                            false);
                                    continue;
                                }
                                // 好友的消息
                                if (chatRecord.getMsgType() == 1) {
                                    chat.addTalkMsgUserLeft(chatRecord.getTalkId(),
                                            chatRecord.getMsgContent(), chatRecord.getMsgType(),
                                            chatRecord.getMsgDate(), true, false,
                                            false);
                                }
                            }
                            break;
                        case 1:
                            // 群组
                            List<ChatRecordDto> groupRecordList = talk.getChatRecordList();
                            if (groupRecordList == null || groupRecordList.isEmpty()) {
                                return;
                            }
                            for (int i = groupRecordList.size() - 1; i >= 0; --i) {
                                ChatRecordDto chatRecord = groupRecordList.get(i);
                                // 自己的消息
                                if (chatRecord.getMsgUserType() == 0) {
                                    chat.addTalkMsgRight(chatRecord.getTalkId(),
                                            chatRecord.getMsgContent(), chatRecord.getMsgType(),
                                            chatRecord.getMsgDate(), true, false,
                                            false);
                                    continue;
                                }
                                // 他人的消息
                                if (chatRecord.getMsgUserType() == 1) {
                                    chat.addTalkMsgGroupLeft(chatRecord.getTalkId(),
                                            chatRecord.getUserId(), chatRecord.getUserNickName(),
                                            chatRecord.getUserHead(), chatRecord.getMsgContent(),
                                            chatRecord.getMsgType(), chatRecord.getMsgDate(),
                                            true, false, false);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                });
            }
            // 群组
            List<GroupsDto> groupsList = msg.getGroupsList();
            if (groupsList != null) {
                groupsList.forEach(groups -> chat.addFriendGroup(groups.getGroupId(),
                        groups.getGroupName(), groups.getGroupHead()));
            }
            // 好友
            List<UserFriendDto> userFriendList = msg.getUserFriendList();
            if (userFriendList != null) {
                userFriendList.forEach(friend -> chat.addFriendUser(false,
                        friend.getFriendId(), friend.getFriendName(),
                        friend.getFriendHead()));
            }
        });
    }
}
