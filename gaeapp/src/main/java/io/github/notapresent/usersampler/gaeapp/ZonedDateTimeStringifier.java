package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.stringifier.Stringifier;

import java.time.ZonedDateTime;

class ZonedDateTimeStringifier implements Stringifier<ZonedDateTime> {
    @Override
    public String toString(ZonedDateTime zdt) {
        return zdt.toString();
    }

    @Override
    public ZonedDateTime fromString(String str) {
        return ZonedDateTime.parse(str);
    }
}