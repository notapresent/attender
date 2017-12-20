package io.github.notapresent.usersampler.common.sampling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserStatusTest {

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

  enum TestStatus implements UserStatus {
    TESTSTATUS
  }
}