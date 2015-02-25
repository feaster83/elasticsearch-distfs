package org.elasticsearch.plugin.distfs.dao;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugin.distfs.exception.FileNotFoundException;
import org.elasticsearch.plugin.distfs.helper.DirectoryMapper;
import org.elasticsearch.plugin.distfs.helper.FileMapper;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.model.DocumentField;
import org.elasticsearch.plugin.distfs.model.File;

import java.util.Map;
import java.util.TreeSet;

import static org.elasticsearch.plugin.distfs.helper.PathUtils.getValidESPath;
import static org.elasticsearch.plugin.distfs.helper.PathUtils.getValidPath;

public class DocumentDAOImpl implements DocumentDAO {

    @Override
    public File findFile(final Client client, final String index, final String type, final String path) throws FileNotFoundException {
        // TODO: improve query/lookup
        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery(DocumentField.PATH, path))
                .execute()
                .actionGet();

        if (response.getHits().getTotalHits() > 0) {
            // TODO: throw error if more than 1 hits?
            Map<String, Object> documentSourceMap = response.getHits().getAt(0).getSource();
            return FileMapper.toFile(documentSourceMap);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public File findFileByPermalink(Client client, String permalink) throws FileNotFoundException {

        SearchResponse searchResponse = client.prepareSearch()
                .setQuery(QueryBuilders.matchQuery("uuid", permalink))
                .execute()
                .actionGet();

        if (searchResponse.getHits().getTotalHits() > 0) {
            // TODO: throw error if more than 1 hits?
            Map<String, Object> documentSourceMap = searchResponse.getHits().getAt(0).getSource();
            return FileMapper.toFile(documentSourceMap);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public Directory findDirectory(Client client, String index, String type, String path) throws FileNotFoundException {

        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.wildcardQuery("path", path + "*"))
                .setSize(Integer.MAX_VALUE)
                .execute()
                .actionGet();

        if (searchResponse.getHits().getTotalHits() > 0) {
           String directoryPath = getValidPath(path);
           String esPath = getValidESPath(path, index, type);

           Directory directory = convertToDirectory(searchResponse, directoryPath, esPath);
           return directory;
        } else {
           throw new FileNotFoundException();
        }
    }

    private Directory convertToDirectory(SearchResponse searchResponse, String directoryPath, String esPath) {
        TreeSet<File> files = new TreeSet<>();
        searchResponse.getHits().forEach(hit -> files.add(FileMapper.toFile(hit.getSource())));

        Directory directory = DirectoryMapper.toDirectory(directoryPath, esPath, files);
        return directory;

    }
}
