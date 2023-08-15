package com.grizz.wooman.reactivestreams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

@Slf4j
public class SimpleHotPublisher implements Flow.Publisher<Integer> {
    private final List<Integer> numbers = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<SimpleHotSubscription> subscriptions = new ArrayList<>();
    private Future task;
    public SimpleHotPublisher() {
        numbers.add(0);
        task = executorService.submit(() -> {
            for (int i = 1; !Thread.interrupted(); i++) {
                numbers.add(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                subscriptions.forEach(SimpleHotSubscription::onNextWhilePossible);
            }
        });

    }

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        var simpleHotSubscription = new SimpleHotSubscription(subscriber);
        subscriptions.add(simpleHotSubscription);
        subscriber.onSubscribe(simpleHotSubscription);
    }

    public void shutdown() {
        task.cancel(true);
        executorService.shutdown();
    }


    public class SimpleHotSubscription implements Flow.Subscription {
        private final Flow.Subscriber<? super Integer> subscriber;
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        private int offset;
        private int requiredOffset;

        public SimpleHotSubscription(Flow.Subscriber<? super Integer> subscriber) {
            int lastElementIndex = numbers.size() - 1;
            this.offset = lastElementIndex;
            this.requiredOffset = lastElementIndex;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            requiredOffset += n;

            onNextWhilePossible();
        }


        private void onNextWhilePossible() {
            executorService.submit(() -> {
                while (offset < requiredOffset || offset < numbers.size()) {
                    var item = numbers.get(offset);
                    subscriber.onNext(item);
                    offset++;
                }
            });
        }

        @Override
        public void cancel() {
            subscriber.onComplete();
            if (subscriptions.contains(this)) {
                subscriptions.remove(this);
            }
            executorService.shutdown();
        }
    }
}
