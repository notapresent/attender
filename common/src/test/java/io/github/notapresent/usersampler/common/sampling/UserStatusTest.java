package io.github.notapresent.usersampler.common.sampling;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserStatusTest {
    enum TestStatus implements UserStatus {
        TESTSTATUS
    }

    @Test
    public void intValueShouldReturnBaseStatusValueUnchanged() {
        BaseStatus gst = BaseStatus.PRIVATE;
        assertEquals(gst.ordinal(), gst.intValue());
    }

    @Test
    public void intValueShouldOffsetSiteStatusValueBySizeOfBaseStatus() {
        UserStatus customStatus = TestStatus.TESTSTATUS;
        assertEquals(BaseStatus.values().length, customStatus.intValue());
    }
}