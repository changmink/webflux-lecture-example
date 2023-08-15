package com.grizz.wooman.reactorpattern.netty;

import com.grizz.wooman.reactorpattern.Acceptor;
import com.grizz.wooman.reactorpattern.EventHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EventLoop implements Runnable{
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final ServerSocketChannel serverSocket;
    private final Selector selector;
    private final EventHandler acceptor;
    @SneakyThrows
    public EventLoop(int port) {
        this.serverSocket = ServerSocketChannel.open();
        this.selector = Selector.open();
        this.serverSocket.bind(new InetSocketAddress("localhost", port));
        this.serverSocket.configureBlocking(false);
        this.acceptor = new Acceptor(selector, serverSocket);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT).attach(acceptor);

    }

    @SneakyThrows
    @Override
    public void run() {
        executorService.submit(() -> {
            while (true) {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

                while (selectedKeys.hasNext()) {
                    var key = selectedKeys.next();
                    selectedKeys.remove();

                    dispatch(key);
                }
            }
        });
    }

    private void dispatch(SelectionKey key) throws IOException {
        if (key.isAcceptable() || key.isReadable()) {
            EventHandler eventHandler = (EventHandler) key.attachment();
            eventHandler.handle();
        }
    }
}
