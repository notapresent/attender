package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.*;
import com.googlecode.objectify.cmd.Saver;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static com.googlecode.objectify.ObjectifyService.ofy;


public class GAESampleStorage implements SampleStorage {
    @Inject
    public GAESampleStorage(SiteRegistry registry) {

    }

    public static void registerEntities() {
        ObjectifyService.register(SampleEntity.class);
        ObjectifyService.register(SiteEntity.class);
    }

    @Override
    public void put(Sample sample) {
        Ref<SiteEntity> parentKey = siteKey(sample.getSite());
        SampleEntity se  = SampleEntity.fromSample(parentKey, sample);
        Result r = ofy().save().entity(se); //.now();  // TODO .now() is for tests, gotta fix this
    }

    @Override
    public Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDateTime day) {     // TODO local date
        Ref<SiteEntity> siteKey = siteKey(site);
        LocalDateTime dayStart = day.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        Date from = Date.from(dayStart.atZone(ZoneOffset.UTC).toInstant());
        Date to = Date.from(nextDayStart.atZone(ZoneOffset.UTC).toInstant());

        List<SampleEntity> samples = ofy().load()
                .type(SampleEntity.class)
                .filter("ts >=", from)
                .filter("ts <", to)
                .order("ts")
                .ancestor(siteKey)
                //.limit(100)
                .list();

        return samples.stream().map((s) -> s.toSample(site)).collect(Collectors.toList());
    }

    private Ref<SiteEntity> siteKey(SiteAdapter site) {
        return Ref.create(Key.create(SiteEntity.class, site.shortName()));
    }

    @Override
    public void deleteFromSiteByDate(SiteAdapter site, LocalDateTime day) { // TODO local date
        Ref<SiteEntity> parent = siteKey(site);
        //Iterable<Key<Car>> allKeys = ofy().load().type(Car.class).keys();
        // keys = [new Key(SampleEntity.class, id, parent) for id in ids ]
        //SampleEntity e = new SampleEntity(parent, SampleStatus.OK, null, null);
    }

    @Override
    public void putAggregateSample(AggregateSample sample) {
        // TODO
    }
}
