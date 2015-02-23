package org.elasticsearch.plugin.distfs.rest.handler;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.distfs.rest.Param;
import org.elasticsearch.plugin.distfs.rest.filter.RequestActionFilter;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

import java.util.Map;
import java.util.TreeMap;

public class IncomingRequestHandler extends BaseRestHandler {
    final Map<String, BaseRestHandler> commandHandlers;
    final BaseRestHandler defaultHandler;

    @Inject
    public IncomingRequestHandler(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
        controller.registerFilter(new RequestActionFilter());

        commandHandlers = new TreeMap<>();

        defaultHandler = new RequestFileHandler(this, settings, client, controller);
        commandHandlers.put("ls", new ListFilesRequestHandler(this, settings, client, controller));
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        String command = request.param(Param.CMD, "").split(" ")[0];

        BaseRestHandler matchingHandler = null;
        if (StringUtils.isNotBlank(command)) {
            logger.debug("Searching for a handler that can handle the '{}' command.", request.param(Param.CMD));
            matchingHandler = commandHandlers.get(command);
        }

        if (matchingHandler == null) {
            matchingHandler = defaultHandler;
        }
        matchingHandler.handleRequest(request, channel);
    }
}
