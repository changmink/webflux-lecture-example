package com.grizz.wooman.selector;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SelectorServer {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);

    @SneakyThrows
    public static void main(String[] args) {
        log.info("start main");
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open(); Selector selector = Selector.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

                while(selectedKeys.hasNext()) {
                    var key = selectedKeys.next();
                    selectedKeys.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = ((ServerSocketChannel)key.channel()).accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()){
                        SocketChannel clientSocket = (SocketChannel) key.channel();
                        handleRequest(clientSocket);
                    }
                }
            }
        }
    }

    @SneakyThrows
    private static void handleRequest(SocketChannel clientSocket) {
            ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);

            clientSocket.read(requestByteBuffer);

            requestByteBuffer.flip();
            String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
            log.info("request: {}", requestBody);

        CompletableFuture.runAsync(() -> {
            try {
            //Thread.sleep(10);

            ByteBuffer responeByteBuffer = ByteBuffer.wrap("This is server".getBytes());

                clientSocket.write(responeByteBuffer);
                clientSocket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}
