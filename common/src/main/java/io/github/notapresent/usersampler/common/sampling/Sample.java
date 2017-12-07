package io.github.notapresent.usersampler.common.sampling;

import java.time.ZonedDateTime;
import java.util.Map;

public class Sample {
    private String site;
    private ZonedDateTime taken;
    private Map<String, Status> userNameToStatus;

    public Sample(String site, ZonedDateTime taken, Map<String, Status> userNameToStatus) {
        this.site = site;
        this.taken = taken;
        this.userNameToStatus = userNameToStatus;
    }
}
