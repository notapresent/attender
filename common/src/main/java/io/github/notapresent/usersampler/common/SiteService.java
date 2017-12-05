package io.github.notapresent.usersampler.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

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

    public List<String> getMessages(String request) {
        List<String> messages = new ArrayList<>();

        try {
            for (SiteAdapter adapter : loader) {
                messages.add(adapter.getAlias());
            }
        } catch (ServiceConfigurationError serviceError) {
            serviceError.printStackTrace();
        }
        return messages;
    }
}
