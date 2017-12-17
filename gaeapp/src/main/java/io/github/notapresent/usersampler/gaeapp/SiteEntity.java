package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name="Site")
class SiteEntity {
    @Id
    public String shortName;
}
