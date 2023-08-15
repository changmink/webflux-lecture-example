package com.grizz.wooman.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer {
    public static void main(String[] args) {
        EventLoopGroup acceptEventGroup = new NioEventLoopGroup();
        EventLoopGroup readEventGroup = new NioEventLoopGroup();
        EventExecutorGroup eventExecutorGroup = new DefaultEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            var server = serverBootstrap.group(acceptEventGroup, readEventGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>(){
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(eventExecutorGroup, new LoggingHandler(LogLevel.INFO));
                    channel.pipeline().addLast(new HttpServerCodec(),
                            new HttpObjectAggregator(1024*1024),
                            new NettyHttpServerHandler()
                    );
                }
            });

            server.bind(8080).sync()
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            log.info("Success");
                        }
                    }).channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            acceptEventGroup.shutdownGracefully();
            readEventGroup.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }
}
