package reactor.reactor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.common.User;
import reactor.reactor.repository.ArticleReactorRepository;
import reactor.reactor.repository.FollowReactorRepository;
import reactor.reactor.repository.ImageReactorRepository;
import reactor.reactor.repository.UserReactorRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class UserReactorServiceTest {
    UserReactorService userService;
    UserReactorRepository userRepository;
    ArticleReactorRepository articleRepository;
    ImageReactorRepository imageRepository;
    FollowReactorRepository followRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserReactorRepository();
        articleRepository = new ArticleReactorRepository();
        imageRepository = new ImageReactorRepository();
        followRepository = new FollowReactorRepository();

        userService = new UserReactorService(
                userRepository, articleRepository, imageRepository, followRepository
        );
    }

    @Test
    void getUserEmptyIfInvalidUserIdIsGiven() throws ExecutionException, InterruptedException {
        // given
        String userId = "invalid_user_id";

        // when
        Optional<User> user = userService.getUserById(userId).blockOptional();

        // then
        assertTrue(user.isEmpty());
    }

    @Test
    void testGetUser() throws ExecutionException, InterruptedException {
        // given
        String userId = "1234";

        // when
        Optional<User> optionalUser = userService.getUserById(userId).blockOptional();

        // then
        assertFalse(optionalUser.isEmpty());
        var user = optionalUser.get();
        assertEquals(user.getName(), "taewoo");
        assertEquals(user.getAge(), 32);

        assertFalse(user.getProfileImage().isEmpty());
        var image = user.getProfileImage().get();
        assertEquals(image.getId(), "image#1000");
        assertEquals(image.getName(), "profileImage");
        assertEquals(image.getUrl(), "https://dailyone.com/images/1000");

        assertEquals(3, user.getArticleList().size());

        assertEquals(1000, user.getFollowCount());
    }
}
