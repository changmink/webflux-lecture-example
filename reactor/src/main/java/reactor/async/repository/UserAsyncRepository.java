package reactor.async.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.common.repository.UserEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UserAsyncRepository {
    private final Map<String, UserEntity> userMap;

    public UserAsyncRepository() {
        var user = new UserEntity("1234", "taewoo", 32, "image#1000");

        userMap = Map.of("1234", user);
    }

    @SneakyThrows
    public CompletableFuture<Optional<UserEntity>> findById(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("UserRepository.findById: {}", userId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            var user = userMap.get(userId);
            return Optional.ofNullable(user);
        });
    }
}
