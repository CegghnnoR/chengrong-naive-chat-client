package org.chengrong.naive.chat.client.event;

import io.netty.channel.Channel;
import org.chengrong.naive.chat.client.infrastructure.util.BeanUtil;
import org.chengrong.naive.chat.client.infrastructure.util.CacheUtil;
import org.chengrong.naive.chat.protocol.login.LoginRequest;
import org.itstack.naive.chat.ui.view.login.ILoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件实现：登录窗口
 */
public class LoginEvent implements ILoginEvent {
    private Logger logger = LoggerFactory.getLogger(LoginEvent.class);

    @Override
    public void doLoginCheck(String userId, String userPassword) {
        Channel channel = BeanUtil.getBean("channel", Channel.class);
        channel.writeAndFlush(new LoginRequest(userId, userPassword));
        CacheUtil.userId = userId;
    }
}
