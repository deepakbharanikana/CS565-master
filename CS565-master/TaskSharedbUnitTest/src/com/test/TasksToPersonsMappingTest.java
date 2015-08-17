package com.test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.Assertion;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

public class TasksToPersonsMappingTest extends  DatabaseTestCase{
	// Provide a connection to the database
			 public static final String TABLE_NAME ="TasksToPersonsMapping";
			private FlatXmlDataSet loadedDataSet;
		    
			protected IDatabaseConnection getConnection() throws Exception{
		     Class.forName("com.mysql.jdbc.Driver");
		      Connection jdbcConnection = 
		      DriverManager.getConnection("jdbc:mysql://localhost/roommate", 
		       "root", "");
		      return new DatabaseConnection(jdbcConnection);
		     
		      
		    
		    }
		    
		 // Load the data which will be inserted for the test
			@Override
			protected IDataSet getDataSet() throws Exception {
				// TODO Auto-generated method stub
				 loadedDataSet= new FlatXmlDataSetBuilder().build(new FileInputStream("TasksToPersonsMapping.xml"));
			     return loadedDataSet;
			}
			
			public void testCheckDataLoaded() throws Exception
			{
			  assertNotNull(loadedDataSet);
			  int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
			  assertEquals(3, rowCount);
			}
			
			//test for dataset
			public void testCompareDataSet() throws Exception
			{
			  IDataSet createdDataSet = getConnection().createDataSet(new String[]
			  {
			    TABLE_NAME
			  });
			  Assertion.assertEquals(loadedDataSet, createdDataSet);
			}
			
			//test for query language whether it is work
			public void testCompareQuery() throws Exception
			{
			  QueryDataSet queryDataSet = new QueryDataSet(getConnection());
			  queryDataSet.addTable(TABLE_NAME, "SELECT task FROM " + TABLE_NAME);
			  Assertion.assertEquals(loadedDataSet, queryDataSet);
			}
			
			public void testQueryResults() throws Exception
			{
			  QueryDataSet queryDataSet = new QueryDataSet(getConnection());
			  queryDataSet.addTable(TABLE_NAME, "SELECT task FROM " + TABLE_NAME);
			  assertNotNull(loadedDataSet);
			  Assertion.assertEquals(loadedDataSet, queryDataSet);
			}
			
}
