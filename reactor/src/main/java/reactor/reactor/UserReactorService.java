package reactor.reactor;

import lombok.RequiredArgsConstructor;
import reactor.common.Article;
import reactor.common.Image;
import reactor.common.User;
import reactor.core.publisher.Mono;
import reactor.reactor.repository.ArticleReactorRepository;
import reactor.reactor.repository.FollowReactorRepository;
import reactor.reactor.repository.ImageReactorRepository;
import reactor.reactor.repository.UserReactorRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserReactorService {
    private final UserReactorRepository userRepository;
    private final ArticleReactorRepository articleRepository;
    private final ImageReactorRepository imageRepository;
    private final FollowReactorRepository followRepository;

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .flatMap(userEntity -> {
                    var image = imageRepository.findById(userEntity.getProfileImageId())
                                .map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()));

                    var articles = articleRepository.findAllByUserId(userEntity.getId())
                                                .map(articleEntity ->
                                                        new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent()))
                                                .collect(Collectors.toList());

                    var followCount = followRepository.countByUserId(userEntity.getId());

                    return Mono.zip(image, articles, followCount)
                            .map(tuple3 -> {
                                    return new User(
                                            userEntity.getId(),
                                            userEntity.getName(),
                                            userEntity.getAge(),
                                            Optional.of(tuple3.getT1()),
                                            tuple3.getT2(),
                                            tuple3.getT3()
                                    );
                            });
                });
    }
}
