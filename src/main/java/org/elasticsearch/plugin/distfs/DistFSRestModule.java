package org.elasticsearch.plugin.distfs;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.plugin.distfs.rest.handler.IncomingRequestHandler;
import org.elasticsearch.plugin.distfs.rest.handler.IndexFileHandler;

public class DistFSRestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IncomingRequestHandler.class).asEagerSingleton();
        bind(IndexFileHandler.class).asEagerSingleton();

    }


}
