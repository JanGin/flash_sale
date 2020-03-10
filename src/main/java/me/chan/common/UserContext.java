package me.chan.common;

import me.chan.domain.User;

public class UserContext {

    private static ThreadLocal<User> holder = new ThreadLocal<>();

    public static void setUser(User user) {
        holder.set(user);
    }

    public static User get() {
        return holder.get();
    }
}
