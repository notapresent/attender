package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.StreamSupport;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class OfySampleStorage implements SampleStorage {
    @Inject
    public OfySampleStorage(SiteRegistry registry) {

    }

    public static void registerEntities() {
        ObjectifyService.register(SampleEntity.class);
        ObjectifyService.register(SiteEntity.class);
    }

    @Override
    public void put(Sample sample) {
        Key<SiteEntity> parentKey = siteKey(sample.getSite());
        SampleEntity se  = SampleEntity.fromSample(parentKey, sample);
        ofy().save().entity(se).now();  // TODO .now() is for tests, gotta fix this
    }

    @Override
    public Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDateTime day) {     // TODO local date
        Key<SiteEntity> ancestor = siteKey(site);
        LocalDateTime dayStart = day.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        Date from = Date.from(dayStart.atZone(ZoneOffset.UTC).toInstant());
        Date to = Date.from(nextDayStart.atZone(ZoneOffset.UTC).toInstant());

        Iterable<SampleEntity> samples = ofy().load()
                .type(SampleEntity.class)
                .filter("ts >=", from)
                .filter("ts <", to)
                .order("ts")
                .ancestor(ancestor)
                //.limit(100)
                .iterable();

        return StreamSupport.stream(samples.spliterator(), false).map((s) -> s.toSample(site))::iterator;
    }


    private Key<SiteEntity> siteKey(SiteAdapter site) {
        return Key.create(SiteEntity.class, site.shortName());
    }

    @Override
    public void deleteFromSiteByDate(SiteAdapter site, LocalDateTime day) { // TODO local date
        Key<SiteEntity> ancestor = siteKey(site);
        LocalDateTime dayStart = day.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        Date from = Date.from(dayStart.atZone(ZoneOffset.UTC).toInstant());
        Date to = Date.from(nextDayStart.atZone(ZoneOffset.UTC).toInstant());

        Iterable<Key<SampleEntity>> keys = ofy().load()
                .type(SampleEntity.class)
                .filter("ts >=", from)
                .filter("ts <", to)
                .order("ts")
                .ancestor(ancestor)
                .keys();
        ofy().delete().keys(keys).now();
    }

    @Override
    public void putAggregateSample(AggregateSample sample) {
        // TODO
    }
}
