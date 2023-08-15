package com.grizz.wooman.completablefuture.async.repository;

import com.grizz.wooman.completablefuture.common.repository.ImageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ImageAsyncRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageAsyncRepository() {
        imageMap = Map.of(
                "image#1000", new ImageEntity("image#1000", "profileImage", "https://dailyone.com/images/1000")
        );
    }

    @SneakyThrows
    public CompletableFuture<Optional<ImageEntity>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("ImageRepository.findById: {}", id);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(imageMap.get(id));
        });

    }
}
