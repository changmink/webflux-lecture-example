package com.grizz.wooman.reactivestreams;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

@Slf4j
public class SimpleHotPublisher implements Flow.Publisher<Integer> {

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {

    }
}
