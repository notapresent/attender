package io.github.notapresent.usersampler.common.site;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

// TODO Refactor this to guice singleton
public class SiteService {
    private static SiteService service;
    private ServiceLoader<SiteAdapter> loader;

    private SiteService() {
        loader = ServiceLoader.load(SiteAdapter.class);
    }

    public static synchronized SiteService getInstance() {
        if (service == null) {
            service = new SiteService();
        }
        return service;
    }

    public List<SiteAdapter> getAdapters() {
        List<SiteAdapter> adapters = new ArrayList<>();
        for (SiteAdapter adapter : loader) {
            adapters.add(adapter);
        }
        return adapters;
    }
}
