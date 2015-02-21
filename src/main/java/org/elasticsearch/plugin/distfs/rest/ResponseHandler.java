package org.elasticsearch.plugin.distfs.rest;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.*;
import org.elasticsearch.search.SearchHits;

import java.util.Base64;
import java.util.Map;

import static org.elasticsearch.plugin.distfs.DistFSPlugin.PLUGIN_PATH;
import static org.elasticsearch.plugin.distfs.rest.DocumentField.CONTENT;
import static org.elasticsearch.plugin.distfs.rest.DocumentField.CONTENT_TYPE;
import static org.elasticsearch.plugin.distfs.rest.Param.*;
import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.NOT_FOUND;
import static org.elasticsearch.rest.RestStatus.OK;

public class ResponseHandler extends BaseRestHandler {


    @Inject
    public ResponseHandler(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
        controller.registerHandler(GET, PLUGIN_PATH, this);
        controller.registerHandler(GET, PLUGIN_PATH + "/{" + INDEX + "}/{" + TYPE + "}/{" + ID + "}", this);
        controller.registerHandler(GET, PLUGIN_PATH + "/permalink/{" + Param.UUID + "}", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("ResponseHandler called");

        Map<String, Object> documentSourceMap = null;

        if (request.param(Param.UUID) != null) {
            // Request by permalink: resolve document by UUID
            SearchHits searchHits = client.prepareSearch()
                                            .setQuery(QueryBuilders.matchQuery("uuid", request.param(Param.UUID)))
                                            .execute()
                                            .actionGet()
                                            .getHits();
                                        
            if (searchHits.getHits().length > 0) {
                documentSourceMap = searchHits.getHits()[0].getSource();
            }
        } else {
            // Request by path: resolve document by INDEX, TYPE and ID
            GetResponse response = client.prepareGet(request.param(INDEX), request.param(TYPE), request.param(ID))
                .execute()
                .actionGet();

            if (response.isExists()) {
                documentSourceMap = response.getSourceAsMap();
            }
        }

        BytesRestResponse restResponse;
        if (documentSourceMap != null) {
            restResponse = buildValidReponse(documentSourceMap);
        } else {
            restResponse = getNotFoundResponse();
        }

        channel.sendResponse(restResponse);
    }

    private BytesRestResponse getNotFoundResponse() {
        return new BytesRestResponse(NOT_FOUND);
    }

    private BytesRestResponse buildValidReponse(Map<String, Object> sourceMap) {
        BytesRestResponse restResponse;
        String contentType = (String) sourceMap.get(CONTENT_TYPE);
        String contentBase64 = (String) sourceMap.get(CONTENT);
        byte[] content = Base64.getDecoder().decode(contentBase64);
        restResponse = new BytesRestResponse(OK, contentType, content);
        return restResponse;
    }
}
