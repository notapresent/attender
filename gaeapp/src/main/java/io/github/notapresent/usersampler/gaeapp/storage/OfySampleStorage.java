package io.github.notapresent.usersampler.gaeapp.storage;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.StreamSupport;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class OfySampleStorage implements SampleStorage {
    @Inject
    public OfySampleStorage(SiteRegistry registry) {

    }

    public static void registerEntities() {
        ObjectifyService.factory().getTranslators().add(new InstantTranslatorFactory());
        ObjectifyService.register(Tube.class);
        ObjectifyService.register(Site.class);
    }

    @Override
    public void put(Sample sample) {
        Tube se = new Tube(
                sample.getSite(),
                sample.getSampleStatus(),
                sample.getTaken(),
                sample
        );

        ofy().save().entity(se).now();  // TODO .now() is for tests, gotta fix this
    }

    @Override
    public Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDate day) {
        Iterable<Tube> entitites = querySamplesForSiteByDay(site, day)
                .order("ts")
                //.limit(100)
                .iterable();

        return StreamSupport.stream(entitites.spliterator(), false)
                .map(Tube::getSample)::iterator;
    }

    private Key<Site> siteKey(SiteAdapter site) {
        return Key.create(Site.class, site.shortName());
    }

    @Override
    public void deleteFromSiteByDate(SiteAdapter site, LocalDate day) {
        Iterable<Key<Tube>> keys = querySamplesForSiteByDay(site, day)
                .keys();
        ofy().delete().keys(keys).now();
    }

    @Override
    public void putAggregateSample(AggregateSample sample) {
        // TODO
    }

    private Query<Tube> querySamplesForSiteByDay(SiteAdapter site, LocalDate day) {
        Key<Site> ancestor = siteKey(site);
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        return ofy().load()
                .type(Tube.class)
                .filter("ts >=", dayStart.toInstant(ZoneOffset.UTC))
                .filter("ts <", nextDayStart.toInstant(ZoneOffset.UTC))
                .ancestor(ancestor);
    }
}
