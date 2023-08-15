package com.grizz.wooman.reactorpattern;

public class Main {
    public static void main(String[] args) {
        Reactor reactor = new Reactor(8080);
        reactor.run();
    }
}
