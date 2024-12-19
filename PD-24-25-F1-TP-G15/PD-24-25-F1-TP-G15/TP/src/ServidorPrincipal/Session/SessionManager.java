package ServidorPrincipal.Session;

import Cliente.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static Map<String, User> userSessions = new HashMap<>();

    public static void addToSession(String sessionId, User user) {
        userSessions.put(sessionId, user);
    }

    public static User getFromSession(String sessionId) {
        return userSessions.get(sessionId);
    }

    public static boolean isUserAuthenticated(String sessionId) {
        return userSessions.containsKey(sessionId);
    }
}
