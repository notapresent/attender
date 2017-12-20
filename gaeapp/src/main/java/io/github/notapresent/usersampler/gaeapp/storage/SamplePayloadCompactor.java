package io.github.notapresent.usersampler.gaeapp.storage;

import io.github.notapresent.usersampler.common.sampling.UserStatus;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SamplePayloadCompactor {

  public static Map<UserStatus, List<String>> deflate(Map<String, UserStatus> payload) {
    return payload.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getValue,
            (e) -> new ArrayList<String>(Collections.singletonList(e.getKey())),
            (left, right) -> {
              left.addAll(right);
              return left;
            }
            )
        );
  }

  public static Map<String, UserStatus> inflate(Map<UserStatus, List<String>> deflated) {

    return deflated.entrySet().stream()
        .flatMap(
            (e) -> e.getValue().stream()
                .map((s) -> new AbstractMap.SimpleEntry<>(e.getKey(), s))
        )
        .collect(Collectors.toMap(
            Map.Entry::getValue,
            Map.Entry::getKey
            )

        );
  }
}
