package org.meross4j.comunication;

import org.jetbrains.annotations.NotNull;
import java.net.http.HttpResponse;
import java.util.Map;

 interface Connector {
    HttpResponse<String> postResponse(Map<String, String> paramsData, String uri, String path);
}

