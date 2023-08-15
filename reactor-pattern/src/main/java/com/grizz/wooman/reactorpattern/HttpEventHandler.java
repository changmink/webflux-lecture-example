package com.grizz.wooman.reactorpattern;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HttpEventHandler implements EventHandler {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final SocketChannel clientSocket;

    @SneakyThrows
    public HttpEventHandler(SocketChannel clientSocket, Selector selector) {
        this.clientSocket = clientSocket;
        clientSocket.configureBlocking(false);
        clientSocket.register(selector, SelectionKey.OP_READ).attach(this);
    }

    @Override
    public void handle() {
        handleRequest(clientSocket);
    }

    @SneakyThrows
    private static void handleRequest(SocketChannel clientSocket) {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);

        clientSocket.read(requestByteBuffer);

        requestByteBuffer.flip();
        String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
        log.info("request: {}", requestBody);
        MsgCodec codec = new MsgCodec();
        String query = codec.decode(requestByteBuffer);

        CompletableFuture.runAsync(() -> {
            try {
                //Thread.sleep(10);

                ByteBuffer responeByteBuffer = codec.encode(query);

                clientSocket.write(responeByteBuffer);
                clientSocket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}
