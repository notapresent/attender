package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class GAESampleStorage implements SampleStorage {
    private Provider<Objectify> ofyProvider;
    private SiteRegistry registry;

    @Inject
    public GAESampleStorage(Provider<Objectify> ofyProvider, SiteRegistry registry) {
        this.ofyProvider = ofyProvider;
        this.registry = registry;
    }

    public static void registerEntities() {
        ObjectifyService.register(SampleEntity.class);
    }

    private Objectify ofy() {
        return ofyProvider.get();
    }

    @Override
    public void put(Sample sample) {
        Key<SiteEntity> parentKey = siteKey(sample.getSite());
        SampleEntity se  = SampleEntity.fromSample(parentKey, sample);
        ofy().save().entity(se);
    }

    @Override
    public List<Sample> getForSiteDate(SiteAdapter site, LocalDateTime day) {
        Key<SiteEntity> siteKey = siteKey(site);
        LocalDateTime dayStart = day.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        Date from = Date.from(dayStart.atZone(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(nextDayStart.atZone(ZoneId.systemDefault()).toInstant());

        List<SampleEntity> samples = ofy().load()
                .type(SampleEntity.class)
                .filter("ts >=", from)
                .filter("ts <", to)
                .order("ts")
                //.limit(100)
                .list();

        return samples.stream().map((s) -> s.toSample(site)).collect(Collectors.toList());
    }

    private Key<SiteEntity> siteKey(SiteAdapter site) {
        return Key.create(SiteEntity.class, site.shortName());
    }

    @Override
    public void deleteByIds(Iterable<Long> ids) {

    }

    @Override
    public void putAggregateSample(AggregateSample sample) {

    }
}
