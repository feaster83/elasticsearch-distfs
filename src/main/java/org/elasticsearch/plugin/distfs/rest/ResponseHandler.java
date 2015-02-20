package org.elasticsearch.plugin.distfs.rest;


import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import java.util.Base64;
import java.util.Map;

import static org.elasticsearch.plugin.distfs.DistFSPlugin.PLUGIN_PATH;
import static org.elasticsearch.plugin.distfs.rest.Param.*;
import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.NOT_FOUND;
import static org.elasticsearch.rest.RestStatus.OK;
import static org.elasticsearch.plugin.distfs.rest.DocumentField.*;

public class ResponseHandler extends BaseRestHandler {


    @Inject
    public ResponseHandler(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
        controller.registerHandler(GET, PLUGIN_PATH, this);
        controller.registerHandler(GET, PLUGIN_PATH + "/{" + INDEX + "}/{" + TYPE + "}/{" + ID + "}", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("ResponseHandler called");

        GetResponse response = client.prepareGet(request.param(INDEX), request.param(TYPE), request.param(ID))
                                     .execute()
                                     .actionGet();

        BytesRestResponse restResponse;
        if (response.isExists()) {
            restResponse = buildValidReponse(response);
        } else {
            restResponse = getNotFoundResponse();
        }

        channel.sendResponse(restResponse);
    }

    private BytesRestResponse getNotFoundResponse() {
        return new BytesRestResponse(NOT_FOUND);
    }

    private BytesRestResponse buildValidReponse(GetResponse response) {
        BytesRestResponse restResponse;Map sourceMap = response.getSourceAsMap();
        String contentType = (String) sourceMap.get(CONTENT_TYPE);
        String contentBase64 = (String) sourceMap.get(CONTENT);
        byte[] content = Base64.getDecoder().decode(contentBase64);
        restResponse = new BytesRestResponse(OK, contentType, content);
        return restResponse;
    }
}
