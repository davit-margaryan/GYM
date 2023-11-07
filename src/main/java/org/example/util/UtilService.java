package org.example.util;


import org.example.model.User;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Component
public class UtilService {

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateUsername(String firstName, String lastName, Map<UUID, User> users) {
        String baseUsername = firstName + "." + lastName;
        String generatedUsername = baseUsername;
        int serialNumber = 1;

        while (usernameExists(users, generatedUsername)) {
            generatedUsername = baseUsername + "." + serialNumber;
            serialNumber++;
        }

        return generatedUsername;
    }

    public String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }

        return password.toString();
    }

    public boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        if (name.trim().isEmpty() || name.length() > 15) {
            return false;
        }
        char firstChar = name.charAt(0);
        return Character.isUpperCase(firstChar);
    }


    public boolean isValid(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public boolean usernameExists(Map<UUID, User> users, String username) {
        for (User user : users.values()) {
            if (user.getUsername() != null && user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public <K> K generateUniqueKey(Map<K, ?> map) {
        K newKey;
        do {
            newKey = (K) UUID.randomUUID();
        } while (map.containsKey(newKey));
        return newKey;
    }

    public void updateFirstName(User user, String firstName) {
        if (isValidName(firstName)) {
            user.setFirstName(firstName);
        }
    }

    public void updateLastName(User user, String lastName) {
        if (isValidName(lastName)) {
            user.setLastName(lastName);
        }
    }

    public void updateUsername(User user, String username, Map<UUID, User> userStorage) {
        if (isValid(username) &&
                !usernameExists(userStorage, username)) {
            user.setUsername(username);
        }
    }

    public void updatePassword(User user, String password) {
        if (isValid(password) &&
                password.length() > 7) {
            user.setPassword(password);
        }
    }
}
