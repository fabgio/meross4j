package org.meross4j.comunication;

import java.net.http.HttpResponse;
import java.util.Map;

 interface HttpConnector {
    HttpResponse<String> postResponse(Map<String, String> paramsData, String uri, String path);
}

