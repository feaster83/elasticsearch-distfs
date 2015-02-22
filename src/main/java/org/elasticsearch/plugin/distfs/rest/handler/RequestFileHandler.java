package org.elasticsearch.plugin.distfs.rest.handler;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.distfs.exception.FileNotFoundException;
import org.elasticsearch.plugin.distfs.model.File;
import org.elasticsearch.plugin.distfs.rest.Param;
import org.elasticsearch.rest.*;

import java.util.Base64;

import static org.elasticsearch.plugin.distfs.DistFSPlugin.PLUGIN_PATH;
import static org.elasticsearch.plugin.distfs.helper.Helpers.documents;
import static org.elasticsearch.plugin.distfs.model.DocumentField.PATH;
import static org.elasticsearch.plugin.distfs.model.DocumentField.UUID;
import static org.elasticsearch.plugin.distfs.rest.Param.INDEX;
import static org.elasticsearch.plugin.distfs.rest.Param.TYPE;
import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.NOT_FOUND;
import static org.elasticsearch.rest.RestStatus.OK;

public class RequestFileHandler extends BaseRestHandler {

    @Inject
    public RequestFileHandler(RestHandler incomingRequestHandler, Settings settings, Client client, RestController controller) {
        super(settings, controller, client);

        controller.registerHandler(GET, PLUGIN_PATH, incomingRequestHandler);
        controller.registerHandler(GET, PLUGIN_PATH + "/{" + INDEX + "}/{" + TYPE + "}", incomingRequestHandler);
        controller.registerHandler(GET, PLUGIN_PATH + "/permalink/{" + Param.UUID + "}", incomingRequestHandler);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("RequestFileHandler called");

        BytesRestResponse restResponse;

        try {
            File file;
            if (request.param(Param.UUID) != null) {
                file = documents().findFileByPermalink(client, request.param(UUID));
            } else {
                file = documents().findFile(client, request.param(INDEX), request.param(TYPE), request.param(PATH));
            }
            restResponse = buildValidResponse(file);
         } catch (FileNotFoundException e) {
            restResponse = getNotFoundResponse();
        }

        channel.sendResponse(restResponse);
    }

    private BytesRestResponse getNotFoundResponse() {
        return new BytesRestResponse(NOT_FOUND);
    }

    private BytesRestResponse buildValidResponse(File file) {
        BytesRestResponse restResponse;
        byte[] content = Base64.getDecoder().decode(file.getContent());
        restResponse = new BytesRestResponse(OK, file.getContentType(), content);
        return restResponse;
    }
}
