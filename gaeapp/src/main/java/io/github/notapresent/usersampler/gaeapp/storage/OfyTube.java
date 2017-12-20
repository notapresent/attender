package io.github.notapresent.usersampler.gaeapp.storage;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Stringify;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import io.github.notapresent.usersampler.common.storage.Tube;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity(name = "Tube")
public class OfyTube implements Tube {

  @Id
  private Long id;
  @Parent
  private Key<Site> parent;
  private SampleStatus st;
  @Index
  private Instant ts;        // taken
  @Stringify(UserStatusStringifier.class)
  private Map<UserStatus, List<String>> pl;     // Payload

  @Ignore
  private String siteId;   // Converted to/from parent

  @Ignore
  private Sample sample;  // Converted to/from payload

  private OfyTube() {
  } // Required by ofy

  public OfyTube(String siteId, Instant taken, Sample sample, SampleStatus status) {
    this.siteId = siteId;
    parent = Key.create(Site.class, siteId);
    st = status;
    ts = taken;
    this.sample = sample;
  }

  public Long getId() {
    return id;
  }

  @Override
  public String getSiteId() {
    return siteId;
  }

  @Override
  public Instant getTaken() {
    return ts;
  }

  public Sample getSample() {
    return sample;
  }

  @Override
  public SampleStatus getStatus() {
    return st;
  }

  @OnSave
  void onSave() {
    pl = SamplePayloadCompactor.deflate(sample.getPayload());
  }

  @OnLoad
  void onLoad() {
    sample = new Sample(SamplePayloadCompactor.inflate(pl), st);
    this.siteId = parent.getName();
  }
}


