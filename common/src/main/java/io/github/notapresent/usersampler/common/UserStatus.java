package io.github.notapresent.usersampler.common;

public class UserStatus {
    public final String userName;
    public final int statusCode;

    public UserStatus(String userName, short statusCode) {
        this.userName = userName;
        this.statusCode = statusCode;
    }
}


