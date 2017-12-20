package io.github.notapresent.usersampler.gaeapp.storage;

import com.googlecode.objectify.stringifier.Stringifier;
import io.github.notapresent.usersampler.common.sampling.UserStatus;

public class UserStatusStringifier implements Stringifier<UserStatus> {

  @Override
  public String toString(UserStatus obj) {
    return obj.qualifiedName();
  }

  @Override
  public UserStatus fromString(String str) {
    return UserStatus.fromName(str);
  }
}

