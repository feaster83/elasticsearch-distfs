package org.elasticsearch.plugin.distfs.rest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
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
        controller.registerFilter(new RequestActionFilter());
        controller.registerHandler(GET, PLUGIN_PATH, this);
        controller.registerHandler(GET, PLUGIN_PATH + "/{" + INDEX + "}/{" + TYPE + "}", this);
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

            // TODO: improve query/lookup
            SearchResponse response = client.prepareSearch(request.param(INDEX))
                    .setTypes(request.param(TYPE))
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery(DocumentField.PATH, request.param(PATH)))
                    .execute()
                    .actionGet();

            if (response.getHits().getTotalHits() > 0) {
                // TODO: throw error if more than 1 hits?
                documentSourceMap = response.getHits().getAt(0).getSource();
            }
        }

        BytesRestResponse restResponse;
        if (documentSourceMap != null) {
            restResponse = buildValidResponse(documentSourceMap);
        } else {
            restResponse = getNotFoundResponse();
        }

        channel.sendResponse(restResponse);
    }

    private BytesRestResponse getNotFoundResponse() {
        return new BytesRestResponse(NOT_FOUND);
    }

    private BytesRestResponse buildValidResponse(Map<String, Object> sourceMap) {
        BytesRestResponse restResponse;
        String contentType = (String) sourceMap.get(CONTENT_TYPE);
        String contentBase64 = (String) sourceMap.get(CONTENT);
        byte[] content = Base64.getDecoder().decode(contentBase64);
        restResponse = new BytesRestResponse(OK, contentType, content);
        return restResponse;
    }
}
