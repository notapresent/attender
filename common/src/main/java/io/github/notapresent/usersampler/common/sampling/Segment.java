package io.github.notapresent.usersampler.common.sampling;

class Segment {

  private final UserStatus status;
  private short length;

  public Segment(UserStatus status, short length) {
    this.status = status;
    this.length = length;
  }

  public Segment(UserStatus status) {
    this.status = status;
    this.length = 0;
  }

  public UserStatus getStatus() {
    return status;
  }

  public short getLength() {
    return length;
  }

  public void grow() {
    length += 1;
  }

}
