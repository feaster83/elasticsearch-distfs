package org.elasticsearch.plugin.distfs;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;

public class DistFSPlugin extends AbstractPlugin {
    public final static String PLUGIN_PATH = "_distfs";

    @Override
    public String name() {
        return "DistFS Plugin";
    }

    @Override
    public String description() {
        return "Simple distributed filesystem located on Elasticsearch.";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = Lists.newArrayList();
        modules.add(DistFSRestModule.class);
        return modules;
    }

}
