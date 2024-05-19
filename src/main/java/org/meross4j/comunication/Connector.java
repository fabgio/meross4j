package org.meross4j.comunication;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface Connector {
    HttpRequest authenticatedPostRequest( URI uriBuilder);
    HttpResponse<String> authenticatedPostResponse(String ... paths);
}

