package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.gaeapp.SampleEntity;


import java.time.ZonedDateTime;

public class GAESampleStorage implements SampleStorage{
    private Provider<Objectify> ofyProvider;

    @Inject
    public GAESampleStorage(Provider<Objectify> ofyProvider) {
        this.ofyProvider = ofyProvider;
    }

    private Objectify ofy() {
        return ofyProvider.get();
    }

    @Override
    public void put(Sample sample) {
        SampleEntity se = SampleEntity.fromSample(sample);
        ofy().save().entity(se);
    }

    @Override
    public Iterable<Sample> getForDate(ZonedDateTime date) {
        return null;
    }

    @Override
    public void deleteByIds(Iterable<Long> ids) {

    }

    @Override
    public void putAggregateSample(AggregateSample sample) {

    }
}
