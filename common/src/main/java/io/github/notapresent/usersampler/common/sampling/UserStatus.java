package io.github.notapresent.usersampler.common.sampling;

public interface UserStatus {
    BaseStatus OFFLINE = BaseStatus.OFFLINE;
    BaseStatus ONLINE = BaseStatus.ONLINE;
    BaseStatus PRIVATE = BaseStatus.PRIVATE;
    BaseStatus PAID = BaseStatus.PAID;

    int ordinal();
    String name();

    default int intValue() {
        if(this instanceof BaseStatus) {
            return ordinal();
        } else {
            return BaseStatus.values().length + ordinal();
        }
    }

    default String strValue() {
        if(this instanceof BaseStatus) {
            return name();
        } else {
            return this.getClass().getSimpleName() + "." + name();
        }
    }

    default String fullName() {
        return this.getClass().getCanonicalName()  + "." + name();
    }

    static UserStatus fromFullName(String fullName) {
        try {
            Class clazz = Class.forName(fullName.split("\\.")[0]);
            String name = fullName.split("\\.")[0];
            return (UserStatus) Enum.valueOf(clazz, name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
