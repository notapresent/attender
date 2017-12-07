package io.github.notapresent.usersampler.common;

import io.github.notapresent.usersampler.common.sampling.GenericStatus;
import io.github.notapresent.usersampler.common.sampling.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatusTest {
    enum CustomStatus implements Status {
        TEST;
    }

    @Test
    public void toIntShouldReturnGenericStatusValueUnchanged() {
        GenericStatus gst = GenericStatus.PRIVATE;
        assertEquals(gst.ordinal(), gst.toInt());
    }

    @Test
    public void toIntShouldOffsetSiteStatusValueBySizeOfGenericStatus() {
        CustomStatus customStatus = CustomStatus.TEST;
        assertEquals(GenericStatus.values().length, customStatus.toInt());

    }
}
