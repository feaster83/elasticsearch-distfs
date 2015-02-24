package org.elasticsearch.plugin.distfs.helper;

import org.elasticsearch.plugin.distfs.dao.DocumentDAO;

import java.util.ServiceLoader;

public class Helpers {
    private static DocumentDAO documentDAO = loadDocumentDAO();

    private static DocumentDAO loadDocumentDAO() {
        return ServiceLoader.load(DocumentDAO.class).iterator().next();
    }

    public static DocumentDAO documents() {
        return documentDAO;
    }

}
