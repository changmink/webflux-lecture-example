package com.grizz.wooman.reactorpattern;

import com.grizz.wooman.reactorpattern.EventHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TcpEventHandler implements EventHandler {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final SocketChannel clientSocket;

    @SneakyThrows
    public TcpEventHandler(SocketChannel clientSocket, Selector selector) {
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
