package io.github.notapresent.usersampler.common.site;

import java.util.*;

// TODO Refactor this to guice singleton
public class SiteRegistry {
    private static SiteRegistry service;
    private ServiceLoader<SiteAdapter> loader;
    private static Map<String, SiteAdapter> aliasToAdapter = new HashMap<>();
    private static Map<String, SiteAdapter> shortNameToAdapter = new HashMap<>();

    private SiteRegistry() {
        loader = ServiceLoader.load(SiteAdapter.class);
    }

    public static synchronized SiteRegistry getInstance() {
        if (service == null) {
            service = new SiteRegistry();
            service.init();
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
