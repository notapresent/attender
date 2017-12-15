package io.github.notapresent.usersampler.common.site;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;

public class SiteRegistry {
    private static final Map<String, SiteAdapter> sites = new HashMap<>();

    public Map<String, SiteAdapter> getSites() {
        if(sites.isEmpty()) {
            loadSites();
        }
        return ImmutableMap.copyOf(sites);
    }

    public static synchronized void loadSites() {
        ServiceLoader<SiteAdapter> loader = ServiceLoader.load(SiteAdapter.class);
        for (SiteAdapter adapter : loader) {
            sites.put(adapter.shortName(), adapter);
        }
    }

    public SiteAdapter getByShortName(String shortName) {
        return getSites().get(shortName);
    }

    public List<SiteAdapter> getAdapters() {
        return ImmutableList.copyOf(getSites().values());
    }
}
