package book.store.util;

import book.store.model.User;

public class UserUtil {
    public static User createUser() {
        return new User()
                .setId(1L)
                .setEmail("test@example.com")
                .setPassword("password")
                .setFirstName("Test")
                .setLastName("User")
                .setShippingAddress("123 Test St");
    }
}
