package com.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;





import org.apache.commons.io.FileUtils;
import org.dbunit.Assertion;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

public class PersonTest extends DatabaseTestCase {
	  // Provide a connection to the database
		 public static final String TABLE_NAME ="Persons";
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
			 loadedDataSet= new FlatXmlDataSetBuilder().build(new FileInputStream("Person.xml"));
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
		  queryDataSet.addTable(TABLE_NAME, "SELECT * FROM " + TABLE_NAME);
		  Assertion.assertEquals(loadedDataSet, queryDataSet);
		}
		
}
