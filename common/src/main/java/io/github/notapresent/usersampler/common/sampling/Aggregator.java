package io.github.notapresent.usersampler.common.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Aggregator {

  private static void processSequence(short position, UserStatus status, List<Segment> currentSeq) {
    Segment currSegment;

    if (currentSeq.isEmpty()) {
      currSegment = new Segment(BaseStatus.OFFLINE, position);

      if (position > 0) {
        currentSeq.add(currSegment);
      }

    } else {
      currSegment = currentSeq.get(currentSeq.size() - 1);
    }

    if (currSegment.getStatus() == status) {
      currSegment.grow();
    } else {
      currentSeq.add(new Segment(status, (short) 1));
    }
  }

  public Map<String, List<Segment>> aggregate(
      Iterable<Map<String, UserStatus>> sampleMaps
  ) {
    short position = 0;
    Map<String, List<Segment>> sequences = new HashMap<>();
    Iterator<Map<String, UserStatus>> it = sampleMaps.iterator();
    List<Segment> currentSeq;

    while (it.hasNext()) {
      for (Map.Entry<String, UserStatus> entry : it.next().entrySet()) {
        currentSeq = sequences.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
        processSequence(position, entry.getValue(), currentSeq);
      }
      position++;
    }
    return sequences;
  }
}