package reactor.async;

import lombok.RequiredArgsConstructor;
import reactor.async.repository.ArticleAsyncRepository;
import reactor.async.repository.FollowAsyncRepository;
import reactor.async.repository.ImageAsyncRepository;
import reactor.async.repository.UserAsyncRepository;
import reactor.common.Article;
import reactor.common.Image;
import reactor.common.User;
import reactor.common.repository.UserEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserAsyncService {
    private final UserAsyncRepository userRepository;
    private final ArticleAsyncRepository articleAsyncRepository;
    private final ImageAsyncRepository imageRepository;
    private final FollowAsyncRepository followAsyncRepository;

    public CompletableFuture<Optional<User>> getUserById(String id) {
        return userRepository.findById(id)
                .thenComposeAsync(user -> {
                    if (user.isEmpty()) {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                    UserEntity userEntity = user.get();

                    var image = imageRepository.findById(userEntity.getProfileImageId())
                                .thenApplyAsync(imageEntityOptional -> {
                                   return imageEntityOptional
                                           .map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()));
                                });

                    var articles = articleAsyncRepository.findAllByUserId(userEntity.getId())
                                        .thenApplyAsync(articleEntities -> articleEntities
                                                .stream()
                                                .map(articleEntity ->
                                                        new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent()))
                                                .collect(Collectors.toList())
                                        );

                    var followCount = followAsyncRepository.countByUserId(userEntity.getId());

                    return CompletableFuture.allOf(image, articles, followCount)
                            .thenApplyAsync(v -> {
                                try {
                                    return Optional.of(new User(
                                            userEntity.getId(),
                                            userEntity.getName(),
                                            userEntity.getAge(),
                                            image.get(),
                                            articles.get(),
                                            followCount.get()
                                    ));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }
}
