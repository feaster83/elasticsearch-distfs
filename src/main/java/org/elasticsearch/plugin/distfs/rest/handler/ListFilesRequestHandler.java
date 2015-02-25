package org.elasticsearch.plugin.distfs.rest.handler;

import lombok.SneakyThrows;
import org.apache.tika.mime.MediaType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.distfs.exception.FileNotFoundException;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.rest.Param;
import org.elasticsearch.rest.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.plugin.distfs.helper.Helpers.documents;
import static org.elasticsearch.rest.RestStatus.BAD_REQUEST;
import static org.elasticsearch.rest.RestStatus.OK;

public class ListFilesRequestHandler extends BaseRestHandler {

    public ListFilesRequestHandler(IncomingRequestHandler incomingRequestHandler, Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("ListFilesRequestHandler called");

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

    @SneakyThrows
    private BytesRestResponse buildValidResponse(Directory directory) {
        BytesRestResponse restResponse;

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();

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

        Template template = ve.getTemplate("templates/velocity/file-list.vm");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        restResponse = new BytesRestResponse(OK, MediaType.TEXT_HTML.toString(), writer.toString());

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
