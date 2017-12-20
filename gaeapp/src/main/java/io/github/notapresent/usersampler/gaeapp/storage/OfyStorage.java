package io.github.notapresent.usersampler.gaeapp.storage;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.storage.SampleStorage;
import io.github.notapresent.usersampler.common.storage.Tube;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class OfyStorage implements SampleStorage {

  public static void registerEntities() {
    ObjectifyService.factory().getTranslators().add(new InstantTranslatorFactory());
    ObjectifyService.register(OfyTube.class);
    ObjectifyService.register(Site.class);
  }

  @Override
  public Long put(Tube tube) {
    Key<Tube> tubeKey = ofy().save().entity(tube).now();
    return tubeKey.getId();
  }

  @Override
  public Iterable<? extends Tube> getForSiteByDate(SiteAdapter site, LocalDate day) {
    return queryTubesFor(site, day)
        .order("ts")
        //.limit(100)   // TODO batching
        .iterable();
  }

  private Key<Site> siteKey(SiteAdapter site) {
    return Key.create(Site.class, site.shortName());

  }

  @Override
  public void deleteFromSiteByDate(SiteAdapter site, LocalDate day) {
    Iterable<Key<OfyTube>> keys = queryTubesFor(site, day)
        .keys();
    ofy().delete().keys(keys).now();
  }

  @Override
  public void putAggregateSample(AggregateSample sample) {
    // TODO
  }

  private Query<OfyTube> queryTubesFor(SiteAdapter site, LocalDate day) {
    Key<Site> ancestor = siteKey(site);
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
