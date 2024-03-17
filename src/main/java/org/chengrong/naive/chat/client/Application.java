package org.chengrong.naive.chat.client;

import io.netty.channel.Channel;
import javafx.stage.Stage;
import org.chengrong.naive.chat.client.application.UIService;
import org.chengrong.naive.chat.client.event.ChatEvent;
import org.chengrong.naive.chat.client.event.LoginEvent;
import org.chengrong.naive.chat.client.infrastructure.util.CacheUtil;
import org.chengrong.naive.chat.client.socket.NettyClient;
import org.chengrong.naive.chat.protocol.login.ReconnectRequest;
import org.itstack.naive.chat.ui.view.chat.ChatController;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;
import org.itstack.naive.chat.ui.view.login.ILoginMethod;
import org.itstack.naive.chat.ui.view.login.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class Application extends javafx.application.Application {
    Logger logger = LoggerFactory.getLogger(Application.class);

    // 默认线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(2);
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1.启动窗口
        IChatMethod chat = new ChatController(new ChatEvent());
        ILoginMethod login = new LoginController(new LoginEvent(), chat);
        login.doShow();

        UIService uiService = new UIService();
        uiService.setChat(chat);
        uiService.setLogin(login);

        // 2.启动socket连接
        logger.info("NettyClient连接服务开始 inetHost：{} inetPort：{}", "127.0.0.1", 7397);
        NettyClient nettyClient = new NettyClient(uiService);
        Future<Channel> future = executorService.submit(nettyClient);
        Channel channel = future.get();
        if (channel == null) {
            throw new RuntimeException("netty client start error channel is null");
        }

        while (!nettyClient.isActive()) {
            logger.info("NettyClient启动服务 ...");
            Thread.sleep(500);
        }

        logger.info("NettyClient连接服务完成 {}", channel.localAddress());

        //Channel 状态定时巡检；2秒后每5s执行一次
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (!nettyClient.isActive()) {
                System.out.println("通信管道巡检：通信管道状态 " + nettyClient.isActive());
                try {
                    System.out.println("通信管道巡检：断线重连【begin】");
                    Channel freshChannel = executorService.submit(nettyClient).get();
                    if (CacheUtil.userId == null) {
                        continue;
                    }
                    freshChannel.writeAndFlush(new ReconnectRequest(CacheUtil.userId));
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("通信管道巡检：断线重连[Error]");
                }
            }
        }, 3, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
