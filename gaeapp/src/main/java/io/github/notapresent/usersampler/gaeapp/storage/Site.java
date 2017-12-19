package io.github.notapresent.usersampler.gaeapp.storage;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name="Site")
public class Site {
    @Id
    public String shortName;
}
