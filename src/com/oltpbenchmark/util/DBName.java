package com.oltpbenchmark.util;

import java.util.Arrays;

import com.oltpbenchmark.catalog.Catalog;

/**
 * Class for wrapping database names. Intended to abstract from case-sensitivity, namespace conventions and escaping 
 * @author tpalmer
 */
public class DBName {
    public static final String DEFAULT_DB_NAME_SEPARATOR = ".";
    
    private static final boolean NORMALIZE_CASE = true;
    
    private final String   elementName;
    private final String[] namespaceNames;
    
    public DBName(String objectName) {
        elementName = NORMALIZE_CASE ? objectName.toUpperCase() : objectName;
        namespaceNames = new String[0];
    }
    
    public DBName(String namespace, String objectName) {
       elementName = NORMALIZE_CASE ? objectName.toUpperCase() : objectName;
       
       if (namespace != null) {
           namespaceNames = new String[1];
           namespaceNames[0] = NORMALIZE_CASE ? namespace.toUpperCase() : namespace;
       } else {
           namespaceNames = new String[0];
       }
    }
    
    public DBName(String objectName, final Catalog containingCatalog) {
        this(containingCatalog.getSchemaName(), objectName);
    }
    
    public String getFullName(final String nameSeparator) {
        StringBuilder sb = new StringBuilder();
        
        for (String nm : namespaceNames) {
            sb.append(nm).append(nameSeparator);
        }
        sb.append(elementName);
        
        return sb.toString();
    }
    
    public String getEscapedFullName(final String nameSeparator) {
        StringBuilder sb = new StringBuilder();
        String esc = Catalog.getSeparator();
        
        sb.append(esc);
        
        for (String nm : namespaceNames) {
            sb.append(nm).append(esc).append(nameSeparator).append(esc);
        }
        sb.append(elementName).append(esc);
        
        return sb.toString();
    }
    
    public String getShortName() {
        return elementName;
    }
    
    public String getNamespaces(final String nameSeparator) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0 ; i < namespaceNames.length ; i++) {
            final String nm = namespaceNames[i];
            sb.append(nm);
            
            if (i < (namespaceNames.length - 1)) {
                sb.append(nameSeparator);
            }
        }
        
        return sb.toString();
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
    
    public boolean equalsIgnoreCase(DBName other) {
        if (other == null) {
            return false;
        }
        
        final String otherStr = other.getFullName(DEFAULT_DB_NAME_SEPARATOR);
        final String ourStr = getFullName(DEFAULT_DB_NAME_SEPARATOR);
        
        return ourStr.equalsIgnoreCase(otherStr);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof DBName) && (obj != null)) {
            DBName other = (DBName)obj;
            return other.elementName.equals(elementName) && other.getFullName(DEFAULT_DB_NAME_SEPARATOR).equals(getFullName(DEFAULT_DB_NAME_SEPARATOR));
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getFullName(DEFAULT_DB_NAME_SEPARATOR).hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getFullName(DEFAULT_DB_NAME_SEPARATOR);
    }
    
    
}
