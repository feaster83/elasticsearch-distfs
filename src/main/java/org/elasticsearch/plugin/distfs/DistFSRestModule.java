package org.elasticsearch.plugin.distfs;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.plugin.distfs.rest.ResponseHandler;

public class DistFSRestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ResponseHandler.class).asEagerSingleton();
    }


}
