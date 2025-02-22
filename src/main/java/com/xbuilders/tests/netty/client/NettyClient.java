package com.xbuilders.tests.netty.client;

import com.xbuilders.tests.netty.packets.requestData.RequestData;
import com.xbuilders.tests.netty.packets.requestData.RequestDataEncoder;
import com.xbuilders.tests.netty.packets.responseData.ResponseDataDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws Exception {

        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new RequestDataEncoder(),
                            new ResponseDataDecoder(), new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

//            for(int i = 0; i < 10; i++) {
//                RequestData d = new RequestData();
//                d.setStringValue("test");
//                f.channel().writeAndFlush(d);
//                Thread.sleep(1000);
//            }

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}