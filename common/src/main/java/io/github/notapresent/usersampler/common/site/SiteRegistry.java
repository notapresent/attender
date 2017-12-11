package io.github.notapresent.usersampler.common.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

// TODO Refactor this to guice singleton
public class SiteRegistry {
    private static SiteRegistry service;
    private ServiceLoader<SiteAdapter> loader;
    private Map<String, SiteAdapter> aliasToAdapter;
    private Map<String, SiteAdapter> shortNameToAdapter;

    private SiteRegistry() {
        loader = ServiceLoader.load(SiteAdapter.class);
    }

    public static synchronized SiteRegistry getInstance() {
        if (service == null) {
            service = new SiteRegistry();
        }
        return service;
    }

    private void init() {
        for (SiteAdapter adapter : loader) {
            aliasToAdapter.put(adapter.getAlias(), adapter);
            shortNameToAdapter.put(adapter.shortName(), adapter);
        }
    }

    public SiteAdapter getByAlias(String alias) {
        return aliasToAdapter.get(alias);
    }

    public SiteAdapter getByShortName(String shortName) {
        return shortNameToAdapter.get(shortName);
    }

    public List<SiteAdapter> getAdapters() {
        return new ArrayList<>(aliasToAdapter.values());
    }
}
