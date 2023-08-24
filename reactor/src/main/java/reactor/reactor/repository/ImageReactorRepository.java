package reactor.reactor.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.common.repository.ImageEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class ImageReactorRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageReactorRepository() {
        imageMap = Map.of(
                "image#1000", new ImageEntity("image#1000", "profileImage", "https://dailyone.com/images/1000")
        );
    }

    @SneakyThrows
    public Mono<ImageEntity> findById(String id) {
        return Mono.fromCallable(() -> {
            log.info("ImageRepository.findById: {}", id);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return imageMap.get(id);
        });

    }
}
