/******************************************************************************
 *  Copyright 2015 by OLTPBenchmark Project                                   *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *    http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 ******************************************************************************/


package com.oltpbenchmark.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.oltpbenchmark.util.DBName;


/**
 * Table Catalog Object
 * @author Carlo A. Curino (carlo@curino.us)
 * @author pavlo
 * @author Djellel
 */
public class Table extends AbstractCatalogObject {
	private static final long serialVersionUID = 1L;
	
	private final List<Column> columns = new ArrayList<Column>();
    private final List<IntegrityConstraint> constraints = new ArrayList<IntegrityConstraint>();
    private final List<String> primaryKeys = new ArrayList<String>();
    private final List<Index> indexes = new ArrayList<Index>();
    private final Map<String, DBName> columnNames = new TreeMap<String, DBName>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DBName> indexNames = new TreeMap<String, DBName>(String.CASE_INSENSITIVE_ORDER);
    
    
    public Table(DBName tableName) {
    	super(tableName);
    }
    
    public Table(Table srcTable) {
        this(srcTable.getName());

        for (int i = 0, cnt = srcTable.columns.size(); i < cnt; i++) {
            Column col = (Column)srcTable.columns.get(i).clone();
            this.columns.add(col);
        } // FOR
        for (IntegrityConstraint ic : srcTable.constraints) {
            this.constraints.add(ic.clone());
        } // FOR
    }
    
    public String getFullyQualifiedName() {
        return getName().getFullName(DBName.DEFAULT_DB_NAME_SEPARATOR);
    }
    
    public String getEscapedFullyQualifiedName() {
        return getName().getEscapedFullName(DBName.DEFAULT_DB_NAME_SEPARATOR);
    }

    @Override
    public Table clone() {
        return new Table(this);
    }
    
    // ----------------------------------------------------------
    // COLUMNS
    // ----------------------------------------------------------
    
    public void addColumn(Column col) {
        assert(this.columns.contains(col) == false) : "Duplicate column '" + col + "'";
        this.columns.add(col);
        DBName colName = col.getName();
        columnNames.put(colName.getShortName(), colName);
    }
    
    public int getColumnCount() {
        return this.columns.size();
    }
    public List<Column> getColumns() {
        return Collections.unmodifiableList(this.columns);
    }
    public Column getColumn(int index) {
        return this.columns.get(index);
    }
    public String getColumnName(int index) {
        return this.columns.get(index).getName().getShortName();
    }
    public int getColumnIndex(Column catalog_col) {
        return (this.getColumnIndex(catalog_col.getName()));
    }
    
    public int getColumnIndex(DBName columnName) {
        for (int i = 0, cnt = getColumnCount(); i < cnt; i++) {
            if (this.columns.get(i).getName().equalsIgnoreCase(columnName)) {
                return (i);
            }
        } // FOR
        return -1;
    }
        
    public int[] getColumnTypes() {
        int types[] = new int[this.getColumnCount()];
        for (Column catalog_col : this.getColumns()) {
            types[catalog_col.getIndex()] = catalog_col.getType();
        } // FOR
        return (types);
    }

    public Column getColumnByName(DBName colname) {
        int idx = getColumnIndex(colname);
        return (idx >= 0 ? this.columns.get(idx) : null);
    }
    
    public Column getColumnByName(String colname) {
        DBName nm = columnNames.get(colname);
        return getColumnByName(nm);
    }

    // ----------------------------------------------------------
    // INDEXES
    // ----------------------------------------------------------
    
    /**
     * Add a new Index for this table
     * @param index
     */
    public void addIndex(Index index) {
        assert(this.indexes.contains(index) == false) : "Duplicate index '" + index + "'";
        this.indexes.add(index);
        indexNames.put(index.getName().getShortName(), index.getName());
    }
    
    public Collection<Index> getIndexes() {
        return (this.indexes);
    }
    
    /**
     * Return the number of indexes for this table
     * @return
     */
    public int getIndexCount() {
        return this.indexes.size();
    }
    
    /**
     * Return a particular index based on its name
     * @param indexName
     * @return
     */
    public Index getIndex(DBName indexName) {
        for (Index catalog_idx : this.indexes) {
            if (catalog_idx.getName().equalsIgnoreCase(indexName)) {
                return (catalog_idx); 
            }
        } // FOR
        return (null);
    }
    
    public Index getIndexByName(String indexName) {
        return getIndex(indexNames.get(indexName));
    }
    
    // ----------------------------------------------------------
    // PRIMARY KEY INDEX
    // ----------------------------------------------------------
    
    
    public void setPrimaryKeyColumns(Collection<String> colNames) {
        this.primaryKeys.clear();
        this.primaryKeys.addAll(colNames);
    }
    
    /**
     * Get the list of column names that are the primary keys for this table
     * @return
     */
    public List<String> getPrimaryKeyColumns() {
        return Collections.unmodifiableList(this.primaryKeys);
    }

    public void addConstraint(List<IntegrityConstraint> iclist) throws IntegrityConstraintsExistsException {
        for (IntegrityConstraint ic : iclist) {
            addConstraint(ic);
        }
    }

    public void addConstraint(IntegrityConstraint ic) throws IntegrityConstraintsExistsException {
        for (IntegrityConstraint c : constraints) {
            if (c != null && c.getId().equals(ic.getId()))
                throw new IntegrityConstraintsExistsException("A constraint " + ic.getId() + " already exists in this table");
        }
        constraints.add(ic);
    }

    @Override
    public boolean equals(Object object) {
        if ((object instanceof Table) == false) return (false);

        Table table2 = (Table)object;
        return (this.name.equals(table2.name) &&
                this.columns.equals(table2.columns) &&
                this.constraints.equals(table2.constraints));
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(getName()).append(" (\n");
        for (int i = 0, cnt = this.columns.size(); i < cnt; i++) {
            sb.append("  ").append(this.columns.get(i)).append("\n");
        } // FOR
        sb.append(")");

        return (sb.toString());
    }
}
