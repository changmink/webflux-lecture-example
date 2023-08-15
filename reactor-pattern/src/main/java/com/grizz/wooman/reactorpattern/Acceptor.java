package com.grizz.wooman.reactorpattern;

import lombok.SneakyThrows;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements EventHandler {
    private final Selector selector;
    private final ServerSocketChannel socketChannel;

    public Acceptor(Selector selector, ServerSocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    @SneakyThrows
    public void handle() {
        SocketChannel clientSocket = socketChannel.accept();
        new TcpEventHandler(clientSocket, selector);
    }
}
