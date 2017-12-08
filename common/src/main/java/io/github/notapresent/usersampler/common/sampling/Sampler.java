package io.github.notapresent.usersampler.common.sampling;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.Error;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.site.FatalSiteError;
import io.github.notapresent.usersampler.common.site.RetryableSiteError;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class Sampler {
    public final int MAX_BATCH_RETRIES = 2;

    private ZonedDateTime startedAt;

    private Set<SiteAdapter> inProgress = new HashSet<>();
    private List<Sample> results = new ArrayList<>();

    private Session session;

    @Inject
    public Sampler(Session session) {
        this.session = session;
    }

    public List<Sample> takeSamples(List<SiteAdapter> adapters) {
        startedAt = ZonedDateTime.now(ZoneOffset.UTC);

        for (SiteAdapter site: adapters) {
            site.reset();
            inProgress.add(site);
        }

        while(!inProgress.isEmpty()) {
            Map<Request, SiteAdapter> batch = makeBatch(inProgress);
            System.out.println("Processing batch " + batch);
            processBatch(batch);
        }

        return results;

//        return adapters.stream().map(this::makeSample).collect(Collectors.toList());
    }

    private Map<Request, SiteAdapter> makeBatch(Collection<SiteAdapter> sites) {
        Map<Request, SiteAdapter> requestBatch = new HashMap<>();
        for (SiteAdapter site: sites
             ) {
            site.getRequests().forEach((r) -> requestBatch.put(r, site));
        }
        return  requestBatch;
    }


    private Map<Request, Future<Response>> multiSend(List<Request> batch) {
        Map<Request, Future<Response>> rv = new HashMap<>();

        for (Request req : batch) {
            // TODO handle request retries here
            rv.put(req, Futures.immediateFuture(session.send(req)));
        }

        return rv;
    }

    private void processBatch(Map<Request, SiteAdapter> batch) {
        int batchRetries = 0;
        Map<Request, SiteAdapter> retryBatch = new HashMap<>();

        while(batchRetries++ < MAX_BATCH_RETRIES && !batch.isEmpty()) {

            @SuppressWarnings({"unchecked"})
            List<Request> requests = new ArrayList(batch.keySet());
            Map<Request, Future<Response>> responseFutures = multiSend(requests);

            for (Map.Entry<Request, Future<Response>> rfEntry: responseFutures.entrySet()) {
                Request request = rfEntry.getKey();
                SiteAdapter site = batch.get(request);
                Future<Response> responseFuture = rfEntry.getValue();

                if(processResponseFuture(site, responseFuture)) {
                    retryBatch.put(request, site);
                }
            }

            batch = retryBatch;
        }

        if(!batch.isEmpty()) {
            for (Request req : batch.keySet()) {
                SiteAdapter site = batch.get(req);
                inProgress.remove(site);
                Throwable err = new FatalSiteError("Failed to fetch " + req);
                results.add(makeSample(site, err));
            }
        }
    }

    private boolean processResponseFuture(SiteAdapter site, Future<Response> respFut) {
        try {
            Response response = respFut.get();
            site.registerResponse(response);

            if (site.isDone()) {
                inProgress.remove(site);
                results.add(makeSample(site));
            }
            return false;
        }

        catch(Error|FatalSiteError e ) {    // Failed request considered a fatal error
            inProgress.remove(site);
            results.add(makeSample(site, e));
            return false;
        }

        catch (RetryableSiteError e) {
            return true;

        } catch (InterruptedException|ExecutionException e) {   // Should never happen
            throw new RuntimeException(e);
        }
    }

    private Sample makeSample(SiteAdapter site) {
        return new Sample(site.getAlias(), startedAt, site.getResult(), Sample.OpStatus.OK);
    }

    private Sample makeSample(SiteAdapter site, Throwable e) {
        return new Sample(site.getAlias(), startedAt, null, Sample.OpStatus.ERROR);
    }

}
