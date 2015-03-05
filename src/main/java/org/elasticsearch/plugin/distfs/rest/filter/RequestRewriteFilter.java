package org.elasticsearch.plugin.distfs.rest.filter;

import org.elasticsearch.http.netty.NettyHttpRequest;
import org.elasticsearch.plugin.distfs.rest.Param;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestFilter;
import org.elasticsearch.rest.RestFilterChain;
import org.elasticsearch.rest.RestRequest;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.elasticsearch.plugin.distfs.DistFSPlugin.PLUGIN_PATH;

public class RequestRewriteFilter extends RestFilter {

    @Override
    public void process(RestRequest request, RestChannel channel, RestFilterChain filterChain) throws Exception {
        if (request.path().startsWith("/" + PLUGIN_PATH)
                && !request.path().startsWith("/" + PLUGIN_PATH + "/permalink")) {

            Pattern p = Pattern.compile("^(\\/"+ PLUGIN_PATH + "\\/[^\\/]+\\/[^?\\/]*)(.*)");
            Matcher m = p.matcher(request.path());
            if (m.matches()) {
                Field pathField = NettyHttpRequest.class.getDeclaredField("rawPath");
                pathField.setAccessible(true);
                pathField.set(request, m.group(1));
                request.params().put(Param.PATH, m.group(2));
            }
        }
        filterChain.continueProcessing(request, channel);
    }
}
