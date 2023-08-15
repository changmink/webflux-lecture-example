package com.grizz.wooman.completablefuture.async;

import com.grizz.wooman.completablefuture.async.repository.ArticleAsyncRepository;
import com.grizz.wooman.completablefuture.async.repository.FollowAsyncRepository;
import com.grizz.wooman.completablefuture.async.repository.ImageAsyncRepository;
import com.grizz.wooman.completablefuture.async.repository.UserAsyncRepository;
import com.grizz.wooman.completablefuture.common.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceAsyncTest {
    UserAsyncService userAsyncService;
    UserAsyncRepository userRepository;
    ArticleAsyncRepository articleRepository;
    ImageAsyncRepository imageRepository;
    FollowAsyncRepository followRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserAsyncRepository();
        articleRepository = new ArticleAsyncRepository();
        imageRepository = new ImageAsyncRepository();
        followRepository = new FollowAsyncRepository();

        userAsyncService = new UserAsyncService(
                userRepository, articleRepository, imageRepository, followRepository
        );
    }

    @Test
    void getUserEmptyIfInvalidUserIdIsGiven() throws ExecutionException, InterruptedException {
        // given
        String userId = "invalid_user_id";

        // when
        Optional<User> user = userAsyncService.getUserById(userId).get();

        // then
        assertTrue(user.isEmpty());
    }

    @Test
    void testGetUser() throws ExecutionException, InterruptedException {
        // given
        String userId = "1234";

        // when
        Optional<User> optionalUser = userAsyncService.getUserById(userId).get();

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

        assertEquals(2, user.getArticleList().size());

        assertEquals(1000, user.getFollowCount());
    }
}
