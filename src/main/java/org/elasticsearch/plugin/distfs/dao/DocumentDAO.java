package org.elasticsearch.plugin.distfs.dao;

import org.elasticsearch.client.Client;
import org.elasticsearch.plugin.distfs.exception.FileNotFoundException;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.model.File;


public interface DocumentDAO {
    File findFile(Client client, String index, String type, String path) throws FileNotFoundException;
    File findFileByPermalink(Client client, String permalink) throws FileNotFoundException;
    Directory findDirectory(Client client, String index, String type, String path) throws FileNotFoundException;
}
