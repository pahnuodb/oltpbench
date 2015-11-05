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

package com.oltpbenchmark.benchmarks.ycsb.procedures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oltpbenchmark.api.Procedure;
import com.oltpbenchmark.api.SQLStmt;
import com.oltpbenchmark.benchmarks.ycsb.YCSBConstants;

public class ScanRecord extends Procedure{
    public final SQLStmt scanStmt = new SQLStmt(
        "SELECT * FROM " + YCSBConstants.YCSB_TABLENAME + " WHERE YCSB_KEY>? AND YCSB_KEY<?"
    );
    
	//FIXME: The value in ysqb is a byteiterator
    public void run(Connection conn, int start, int count, List<Map<Integer,String>> results) throws SQLException {
        PreparedStatement stmt = this.getPreparedStatement(conn, scanStmt);
        stmt.setInt(1, start); 
        stmt.setInt(2, start+count); 
        ResultSet r=stmt.executeQuery();
        while(r.next())
        {
        	HashMap<Integer,String> m=new HashMap<Integer,String>();
        	for(int i=1;i<11;i++)
        		m.put(i, r.getString(i));
        	results.add(m);
        }
        r.close();
    }
}
