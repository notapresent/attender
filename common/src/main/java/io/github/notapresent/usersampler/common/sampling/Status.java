package io.github.notapresent.usersampler.common.sampling;

/* Marker interface to designate user status enums */
public interface Status {
    /* Mimic enum values */
    String name();
    int ordinal();

    default int toInt() {
        int minCustomStatus = GenericStatus.values().length;
        if (this instanceof GenericStatus) {
            return ordinal();
        } else {
            return minCustomStatus + ordinal();
        }
    }
}
