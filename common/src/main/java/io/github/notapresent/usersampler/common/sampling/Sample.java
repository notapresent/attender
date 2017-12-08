package io.github.notapresent.usersampler.common.sampling;

import java.time.ZonedDateTime;
import java.util.Map;

public class Sample {
    private String siteAlias;
    private ZonedDateTime taken;
    private Map<String, Status> userNameToStatus;
    private OpStatus opStatus;

    public String getSiteAlias() {
        return siteAlias;
    }

    public ZonedDateTime getTaken() {
        return taken;
    }

    public Map<String, Status> getUserNameToStatus() {
        return userNameToStatus;
    }

    public OpStatus getStatus() {
        return opStatus;
    }

    public Sample(String siteAlias, ZonedDateTime taken, Map<String, Status> userNameToStatus, OpStatus opStatus) {
        this.siteAlias = siteAlias;
        this.taken = taken;
        this.userNameToStatus = userNameToStatus;
        this.opStatus = opStatus;
    }

    public enum OpStatus {
        OK,
        WARNING,
        ERROR,
    }
}
