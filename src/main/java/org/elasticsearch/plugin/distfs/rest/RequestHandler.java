package org.elasticsearch.plugin.distfs.rest;


import net.sf.jmimemagic.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.plugin.distfs.DistFSPlugin.PLUGIN_PATH;
import static org.elasticsearch.plugin.distfs.rest.Param.*;
import static org.elasticsearch.rest.RestRequest.Method.POST;

public class RequestHandler extends BaseRestHandler {

    @Inject
    public RequestHandler(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
        controller.registerHandler(POST, PLUGIN_PATH + "/{" + INDEX + "}/{" + TYPE + "}/{" + ID + "}", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("RequestHandler called");

        BytesRestResponse restResponse = null;
        if (request.hasContent()) {
            restResponse = indexContent(request, client);
        } else {
            restResponse = new BytesRestResponse(RestStatus.BAD_REQUEST);
        }

        channel.sendResponse(restResponse);
    }

    private BytesRestResponse indexContent(RestRequest request, Client client) throws IOException {

        String contentType = getContentType(request);
        String contentBase64 = getContentAsBase64(request);

        BytesRestResponse restResponse = null;
        if (StringUtils.isNotBlank(contentType)) {
           restResponse = addFileToIndex(request, client, contentType, contentBase64);
        } else {
           restResponse = new BytesRestResponse(RestStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        return restResponse;
    }

    private BytesRestResponse addFileToIndex(RestRequest request, Client client, String contentType, String contentBase64) throws IOException {
        IndexResponse indexResponse = client.prepareIndex(request.param(INDEX), request.param(TYPE), request.param(ID))
                .setSource(jsonBuilder()
                        .startObject()
                        .field(DocumentField.CONTENT_TYPE, contentType)
                        .field(DocumentField.CONTENT, contentBase64)
                        .endObject())
                .execute()
                .actionGet();

        BytesRestResponse restResponse;
        if (indexResponse.isCreated()) {
            restResponse = new BytesRestResponse(RestStatus.CREATED);
        } else if (indexResponse.getVersion() > 0) {
            restResponse = new BytesRestResponse(RestStatus.ACCEPTED);
        } else {
            restResponse = new BytesRestResponse(RestStatus.BAD_REQUEST);
        }
        return restResponse;
    }

    private String getContentType(RestRequest request) {
        String contentType = null;
        try {
           MagicMatch m = Magic.getMagicMatch(request.content().toBytes());
           contentType = m.getMimeType();
        } catch (MagicParseException e) {
           logger.error(e.getMessage(), e);
        } catch (MagicMatchNotFoundException e) {
           logger.error(e.getMessage(), e);
        } catch (MagicException e) {
           logger.error(e.getMessage(), e);
        }
        return contentType;
    }

    private String getContentAsBase64(RestRequest request) throws IOException {
        InputStream is = request.content().streamInput();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] chunk = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(chunk)) > 0) {
            os.write(chunk, 0, bytesRead);
        }
        os.flush();

        return Base64.getEncoder().encodeToString(os.toByteArray());
    }

}
