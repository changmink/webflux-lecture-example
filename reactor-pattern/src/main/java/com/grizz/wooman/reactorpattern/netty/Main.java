package com.grizz.wooman.reactorpattern.netty;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<EventLoop> eventLoops = List.of(new EventLoop(8081), new EventLoop(8082));
        eventLoops.forEach(EventLoop::run);
    }
}
