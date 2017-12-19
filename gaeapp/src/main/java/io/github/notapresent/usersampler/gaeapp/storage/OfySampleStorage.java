package io.github.notapresent.usersampler.gaeapp.storage;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.sampling.SampleTube;
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
        ObjectifyService.register(OfyTube.class);
        ObjectifyService.register(Site.class);
    }

    @Override
    public void put(Sample sample, LocalDateTime taken) {
        OfyTube se = new OfyTube(
                sample.getSite().shortName(),
                sample.getSampleStatus(),
                taken,
                sample
        );

        ofy().save().entity(se).now();  // TODO .now() is for tests, gotta fix this
    }

    @Override
    public void put(SampleTube tube) {
        // TODO
    }

    @Override
    public Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDate day) {
        Iterable<OfyTube> entitites = querySamplesForSiteByDay(site, day)
                .order("ts")
                //.limit(100)
                .iterable();

        return StreamSupport.stream(entitites.spliterator(), false)
                .map(OfyTube::getSample)::iterator;
    }

    private Key<Site> siteKey(SiteAdapter site) {
        return Key.create(Site.class, site.shortName());

    }

    @Override
    public void deleteFromSiteByDate(SiteAdapter site, LocalDate day) {
        Iterable<Key<OfyTube>> keys = querySamplesForSiteByDay(site, day)
                .keys();
        ofy().delete().keys(keys).now();
    }

    @Override
    public void putAggregateSample(AggregateSample sample) {
        // TODO
    }

    private Query<OfyTube> querySamplesForSiteByDay(SiteAdapter site, LocalDate day) {
        Key<Site> ancestor = siteKey(site);
        System.out.println("Querying with ancestor " + ancestor);
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime nextDayStart = dayStart.plusDays(1);

        return ofy().load()
                .type(OfyTube.class)
                .filter("ts >=", dayStart.toInstant(ZoneOffset.UTC))
                .filter("ts <", nextDayStart.toInstant(ZoneOffset.UTC))
                .ancestor(ancestor)
                ;

    }
}
