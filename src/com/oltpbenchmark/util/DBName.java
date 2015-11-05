package com.oltpbenchmark.util;

import com.oltpbenchmark.catalog.Catalog;

/**
 * Class for wrapping database names. Intended to abstract from case-sensitivity, namespace conventions and escaping 
 * @author tpalmer
 */
public class DBName {
    private final String   elementName;
    private final String   sep;
    private final String[] namespaceNames;
    
    public DBName(String objectName, String namespace, String nameSeparator) {
       elementName = objectName;
       namespaceNames = new String[1];
       namespaceNames[0] = namespace;
       sep = nameSeparator;
    }
    
    public DBName(String objectName, Catalog containingCatalog, String nameSeparator) {
        this(objectName, containingCatalog.getSchemaName(), nameSeparator);
    }
    
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        
        for (String nm : namespaceNames) {
            sb.append(nm).append(sep);
        }
        sb.append(elementName);
        
        return sb.toString();
    }
    
    public String getShortName() {
        return elementName;
    }
    
    /**
     * Kludge until we get rid of all the strings in the benchmarks
     */
    public static String getFullName(String name, Catalog cat, String separator) {
        StringBuilder sb = new StringBuilder();
        
        if (cat.getSchemaName() != null) {
            sb.append(cat.getSchemaName()).append(separator);
        }
        
        sb.append(name);
        
        return sb.toString();
    }
}
