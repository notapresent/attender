package io.github.notapresent.usersampler.common.HTTP;

public class Util {
    public static boolean isRedirect(int code) {
        return code >= 301 && code <= 303;
    }
}
