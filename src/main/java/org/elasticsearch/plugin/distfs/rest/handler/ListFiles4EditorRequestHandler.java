package org.elasticsearch.plugin.distfs.rest.handler;

import org.apache.tika.mime.MediaType;
import org.apache.velocity.VelocityContext;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.distfs.exception.FileNotFoundException;
import org.elasticsearch.plugin.distfs.helper.VelocityHelper;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.rest.Param;
import org.elasticsearch.rest.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.plugin.distfs.helper.Helpers.documents;
import static org.elasticsearch.rest.RestStatus.BAD_REQUEST;
import static org.elasticsearch.rest.RestStatus.OK;

public class ListFiles4EditorRequestHandler extends BaseRestHandler {

    public ListFiles4EditorRequestHandler(IncomingRequestHandler incomingRequestHandler, Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("ListFiles4EditorRequestHandler called");

        String directoryPath = request.param(Param.PATH);

        RestResponse response;
        try {
            Directory directory = documents().findDirectory(client, request.param(Param.INDEX), request.param(Param.TYPE), directoryPath);
            response = buildValidResponse(directory);
        } catch (FileNotFoundException e) {
            response = buildInvalidResponse(directoryPath);
        }

        channel.sendResponse(response);
    }

    private boolean isDirectory(String path) {
        return false;
    }

    private BytesRestResponse buildInvalidResponse(String directory) {
        return new BytesRestResponse(BAD_REQUEST, "text/html", "Invalid request: ["+directory+"] is not a directory.");
    }


    private BytesRestResponse buildValidResponse(Directory directory) {
        BytesRestResponse restResponse;

        ArrayList fileList = new ArrayList();

        if (directory.getPath().length() > 1) {
            String pathUp = directory.getEsPath().substring(0, directory.getEsPath().lastIndexOf("/"));
            fileList.add(getFileDef("up", "..", pathUp, "", ""));
        }

        directory.getSubDirectories().forEach(subdir ->
                        fileList.add(getFileDef("dir", subdir.getName(), subdir.getEsPath(), "", ""))
        );

        directory.getFiles().forEach(file ->
            fileList.add(getFileDef("file", file.getFileName(), directory.getEsPath() + "/" + file.getFileName(), file.getContentType(), file.getUuid()))
        );

        VelocityContext context = new VelocityContext();
        context.put("directoryName", directory.getPath());
        context.put("fileList", fileList);
        String output = VelocityHelper.renderTemplate("file-list-editor.vm", context);

        restResponse = new BytesRestResponse(OK, MediaType.TEXT_HTML.toString(), output);

        return restResponse;
    }

    private Map<String, String> getFileDef(String type, String filename, String filepath, String contentType, String uuid) {
        Map<String, String> fileDef = new HashMap();
        fileDef.put("filename", filename);
        fileDef.put("filepath", filepath);
        fileDef.put("filetype", type);
        fileDef.put("contentType", contentType);
        fileDef.put("uuid", uuid);
        return fileDef;
    }

}
