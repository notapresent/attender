package io.github.notapresent.usersampler.common;

import java.util.*;

public class SiteService {
    private static SiteService service;
    private ServiceLoader<SiteServiceProvider> loader;

    private SiteService() {
        loader = ServiceLoader.load(SiteServiceProvider.class);
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
            for (SiteServiceProvider adapter : loader) {
                messages.add(adapter.getMessage(request));
            }
        } catch (ServiceConfigurationError serviceError) {
            serviceError.printStackTrace();
        }
        return messages;
    }
}
