package io.github.notapresent.usersampler.HTTP;

import java.io.IOException;

public interface Session <R extends Request>{
    Response send(R request) throws IOException;
}
