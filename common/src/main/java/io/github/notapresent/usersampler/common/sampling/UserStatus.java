package io.github.notapresent.usersampler.common.sampling;

import java.util.HashMap;
import java.util.Map;

public class UserStatus {
    private static final Map<Integer, UserStatus> value2status = new HashMap<>();
    private static final Map<String, UserStatus> name2status = new HashMap<>();

    public static final UserStatus OFFLINE = new UserStatus(0, "OFFLINE");
    public static final UserStatus ONLINE = new UserStatus(1, "ONLINE");
    public static final UserStatus PAID = new UserStatus(2, "PAID");
    public static final UserStatus PRIVATE = new UserStatus(3, "PRIVATE");

    private final int value;
    private final String name;


    protected UserStatus(int value, String name) {
        this.name = name;
        this.value = value;
        if(value2status.containsKey(value)) {
            throw new IllegalArgumentException("UserStatus with value " + value + "already registered");
        }
        value2status.put(value, this);
        name2status.put(name, this);
    }

    public static UserStatus fromName(String name) {
        return name2status.get(name);
    }

    public static UserStatus fromValue(int value) {
        return value2status.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
