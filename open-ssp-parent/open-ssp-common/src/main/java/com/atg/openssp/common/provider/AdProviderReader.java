package com.atg.openssp.common.provider;

import com.atg.openssp.common.core.entry.SessionAgent;

/**
 * 
 * @author André Schmer, TrieuNT
 *
 */
public interface AdProviderReader {

    public static final String APPLICATION_JAVASCRIPT = "application/javascript";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";

    float getPrice();

    float getPriceEur();

    String getCurrrency();

    void perform(SessionAgent agent);

    String buildResponse();

    String getVendorId();

    boolean isValid();

    String getAdid();

    String getContentType();

}
