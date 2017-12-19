package io.github.notapresent.usersampler.common.sampling;

public interface UserStatus {
    String SEPARATOR = ":";

    int ordinal();
    String name();

    default int intValue() {
        if(this instanceof BaseStatus) {
            return ordinal();
        } else {
            return BaseStatus.values().length + ordinal();
        }
    }

    default String qualifiedName() {
        if(this instanceof BaseStatus) {
            return name();
        } else {
            return this.getClass().getCanonicalName() + SEPARATOR + name();
        }
    }

    @SuppressWarnings("unchecked")
    static UserStatus fromName(String name) {
        if(name.contains(".")) {
            try {
                Class clazz = Class.forName(name.split(SEPARATOR)[0]);
                String strName = name.split(SEPARATOR)[1];
                return (UserStatus) Enum.valueOf(clazz, strName);
            } catch (ClassNotFoundException e) {
                return null;
            }
         } else {
            return BaseStatus.valueOf(name);
        }
    }
}
