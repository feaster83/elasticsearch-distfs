package org.elasticsearch.plugin.distfs.rest;


import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class ResponseHandler extends BaseRestHandler {

    @Inject
    public ResponseHandler(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);

        // Define REST endpoints
        controller.registerHandler(GET, "/_distfs/", this);
        controller.registerHandler(GET, "/_distfs/{name}", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("ResponseHandler called");

        BytesRestResponse restResponse = new BytesRestResponse(OK, "text/html", "<html><head><title>DistFS active</title></head><body>DistFS is active</body></html>");
        channel.sendResponse(restResponse);
    }
}
