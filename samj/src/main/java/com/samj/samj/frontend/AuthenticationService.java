package com.samj.samj.frontend;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private final Map<String, String> userStore;

    public AuthenticationService() {
        this.userStore = new HashMap<>();
        initializeUsers();
    }

    private void initializeUsers() {
        // Pre-populate with some users (username, password)
        // In a real application, these should be securely hashed
        userStore.put("admin_test", "test1000!");
        userStore.put("user2", "password2!");
        // Add more users as needed
    }

    public boolean authenticate(String username, String password) {
        String storedPassword = userStore.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
}