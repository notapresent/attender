package io.github.notapresent.usersampler.common.HTTP;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class Response {
    protected int status;
    protected byte[] content;
    protected Map<String, String> headers;
    protected String finalUrl;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    protected Request request;

    public Response(int status, byte[] content, String final_url) {
        this(status, Collections.EMPTY_MAP, content, final_url);
    }

    public Response(int status, Map<String, String> headers, byte[] content, String finalUrl) {
        this.status = status;
        this.headers = headers;
        this.content = content;
        this.finalUrl = finalUrl;
    }

    public int getStatus() {
        return status;
    }

    public byte[] getContentBytes() {
        return content;
    }

    public String getFinalUrl() {
        return finalUrl;
    }

    public void setFinalUrl(String url) {
        finalUrl = url;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getContentString() {
        return new String(getContentBytes(), Charsets.UTF_8);
    }

    public String getContentString(Charset charSet) {
        return new String(getContentBytes(), charSet);
    }
}
