package com.programm.vertx.repository.inmemory;

import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class UserRepositoryTest {

    @Test
    void addAndGetUser() {
        UserRepository userRepository = new UserRepository();
        User user = new User(UUID.randomUUID().toString());
        userRepository.add(user).await().indefinitely();
        User givenUser = userRepository.get(user.getId().toString()).await().indefinitely();
        Assertions.assertEquals(user, givenUser);
    }

    @Test
    void throwErrorIfUserNotFound() {
        UserRepository userRepository = new UserRepository();

        UniAssertSubscriber<User> subscriber = userRepository.get("asdqwe")
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(EntityNotFoundException.class);
    }

    @Test
    void findAll() {
        UserRepository userRepository = new UserRepository();

        Map<String, User> expectedMap = new HashMap<>();
        List<User> users = List.of(new User(UUID.randomUUID().toString()), new User(UUID.randomUUID().toString()));

        users.forEach((user) -> {
            expectedMap.put(user.getId().toString(), user);
            userRepository.add(user).await().indefinitely();
        });

        UniAssertSubscriber<Map<String, User>> assertSubscriber = userRepository.findAll().subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted().assertItem(expectedMap);
    }

    @Test
    void notImplementedException() {
        UserRepository userRepository = new UserRepository();
        Assertions.assertThrows(RuntimeException.class, () -> userRepository.getUsersByGroup(null));
        Assertions.assertThrows(RuntimeException.class, () -> userRepository.findByIds(null));
    }

    @Test
    void testDeletion() {
        UserRepository userRepository = new UserRepository();
        User user = new User(UUID.randomUUID().toString());
        userRepository.add(user).await().indefinitely();
        userRepository.delete(user).await().indefinitely();

        UniAssertSubscriber<User> assertSubscriber = userRepository.find(user.getStringId())
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted().assertItem(null);
    }

    @Test
    void testUpdate() {
        UserRepository userRepository = new UserRepository();
        String uuid = UUID.randomUUID().toString();
        User user1 = new User(uuid);
        User user2 = new User(uuid);
        user2.setLogin("asdqwe");

        userRepository.add(user1).await().indefinitely();
        userRepository.update(user2).await().indefinitely();

        UniAssertSubscriber<User> assertSubscriber = userRepository.find(user1.getStringId())
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted().assertItem(user2);
    }

    @Test
    void testFindByPrefixSuccess() {
        UserRepository userRepository = new UserRepository();

        User user1 = new User(UUID.randomUUID().toString());
        user1.setLogin("sdf");

        User user2 = new User(UUID.randomUUID().toString());
        user2.setLogin("asdqwe");

        userRepository.add(user1).await().indefinitely();
        userRepository.add(user2).await().indefinitely();

        userRepository.add(user1).await().indefinitely();
        userRepository.update(user2).await().indefinitely();


        UsersFilterRequest usersFilterRequest = new UsersFilterRequest("asd", "1", "2");

        UniAssertSubscriber<ResponsePaginatedWrapper<Map<String, UserResponse>>> assertSubscriber = userRepository
                .findByPrefix(usersFilterRequest)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }
}