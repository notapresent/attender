package io.github.notapresent.usersampler.common.sampling;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusTest {
    private UserStatus standardStatus = UserStatus.ONLINE;
    private CustomStatus customStatus = CustomStatus.CUSTOM;

    @Test
    public void fromValueShouldReturnCorrectValue() {
        assertEquals(standardStatus, UserStatus.fromValue(standardStatus.getValue()));
    }

    @Test
    public void fromValueShouldReturnCorrectCustomValue() {
        assertEquals(customStatus, UserStatus.fromValue(customStatus.getValue()));
    }
}

class CustomStatus extends UserStatus {
    private CustomStatus(int value, String name) { super(value, name); }
    public static final CustomStatus CUSTOM = new CustomStatus(4, "CUSTOM");
}
