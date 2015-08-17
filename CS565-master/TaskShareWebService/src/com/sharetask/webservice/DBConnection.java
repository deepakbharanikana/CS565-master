package com.sharetask.webservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class DBConnection {

	@SuppressWarnings("finally")
	public static Connection createConnection() throws Exception{
		Connection con = null;
		try{
			Class.forName(Constants.dbClass);
			con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPassword);
		}
		catch (Exception e){
			throw e;
		}
		finally{
			return con;
		}
	}
	
	public static boolean userAuthentication(String userName, String password) throws Exception{
		boolean isAvailabe = false;
		Connection con = null;
		try{
			try{
				con = DBConnection.createConnection();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT * FROM persons WHERE EmailId = '"+userName+"' AND password = "+"'"+password+"'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				isAvailabe = true;
			}
		}
		
		catch(SQLException sqlExcep){
			throw sqlExcep;
		}
		catch (Exception e) {
			throw e;
		}
		
		finally 
		{
			if(con != null){
				con.close();
			}
		}
		return isAvailabe;
	}
	
	// ToDo : Modify to send paramters from sign up page
	public static boolean createUser(String name, String email, String password) throws Exception{
		boolean isCreated = false;
		Connection con = null;
		try{
			try{
				con = DBConnection.createConnection();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "INSERT into persons(EmailId, password, LastName, FirstName, City) values('"
					+ name + "'," + "'" + email + "','" + password + "')";
			
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				isCreated = true;
			}
		}
		catch (SQLException sqlExcep)
		{
			throw sqlExcep;
		}
		catch (Exception e) 
		{
			throw e;
		}
		finally 
		{
			if (con != null) 
			{
				con.close();
			}
		}
		return isCreated;
	}
	
	public static ArrayList<String> getUserGroups(String email) throws Exception {
		Connection con = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT GroupName FROM groupstopersonsmapping where EmailId = '"+email+"'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				list.add(rs.getString("GroupName"));
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}
	
	public static boolean createGroup(String name, String email)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*Statement stmt1 = con.createStatement();
			String query1 = "INSERT into groups(GroupName) values('"+ name + "')";
			int records1 = stmt1.executeUpdate(query1);*/
			
			Statement stmt2 = con.createStatement();
			String query2 = "INSERT into groupstopersonsmapping(EmailId,GroupName) values('"
					+ email + "'," + "'" + name + "')";
			int records2 = stmt2.executeUpdate(query2);
			if ( records2 > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static ArrayList<String> getPersonsInGroup(String name) throws Exception {
		Connection con = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT EmailId,minPoints,PendingPoints,RecurrenceStartDate FROM groupstopersonsmapping WHERE GroupName = '" + name + "' ";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				list.add(rs.getString("EmailId") + "*#*#*#*" + rs.getString("minPoints")+ "~#~#~#~" + rs.getString("PendingPoints")+ "%#%#%#%" + rs.getString("RecurrenceStartDate") );
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}
	
	public static boolean createPersonsInGroup(String email, String groupname, String weeklyrecurvisepoints, String RecurrenceStart)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "INSERT into groupstopersonsmapping(EmailId,GroupName, minPoints, RecurrenceStartDate) values('"
					+ email + "'," + "'" + groupname + "'," + "'" + weeklyrecurvisepoints + "'," + "'" + RecurrenceStart + "')";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static boolean createPersonsMinPoints(String email, String groupname, String minpoints)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "UPDATE groupstopersonsmapping SET minPoints = '"+minpoints+"' WHERE  GroupName = '" + groupname + "' AND EmailId = '" + email + "'";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static boolean updateTaskCompletion(String email, String taskname, String iscompleted, String groupname)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "UPDATE taskstopersonsmapping SET MarkCompletion = '"+iscompleted+"' WHERE  GroupName = '" + groupname + "' AND EmailId = '" + email + "' AND TaskName = '" + taskname + "'";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static boolean autoAdjustPoints(String name, String selecteTaskName, String totalDelta, String totalTaskPoints, String selectedTaskPoints) throws Exception {
		Connection con = null;
		boolean createStatus = false;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT TaskName,TaskPoints FROM tasks WHERE GroupName = '" + name + "' ";
			ResultSet rs = stmt.executeQuery(query);
			DecimalFormat numberFormat = new DecimalFormat("#.00");
			while (rs.next()) { 
				if(rs.getString("TaskName").equals(selecteTaskName))
				{
					Double modifiedPoints = Double.parseDouble(rs.getString("TaskPoints")) + Double.parseDouble(totalDelta);
					modifiedPoints = Double.parseDouble(numberFormat.format(modifiedPoints));
					Statement stmt1 = con.createStatement();
					String query1 = "UPDATE tasks SET TaskPoints = '"+modifiedPoints.toString()+"' WHERE  GroupName = '" + name + "' AND TaskName = '" + rs.getString("TaskName") + "'";
					int records =stmt1.executeUpdate(query1);
					if (records > 0) {
						createStatus = true;
					}
				}
				else{
					Double modifiedPoints = Double.parseDouble(rs.getString("TaskPoints")) + ((-1)*Double.parseDouble(totalDelta)*Double.parseDouble(rs.getString("TaskPoints")))/(Double.parseDouble(totalTaskPoints)-Double.parseDouble(selectedTaskPoints));
					modifiedPoints = Double.parseDouble(numberFormat.format(modifiedPoints));
					Statement stmt1 = con.createStatement();
					String query1 = "UPDATE tasks SET TaskPoints = '"+modifiedPoints.toString()+"' WHERE  GroupName = '" + name + "' AND TaskName = '" + rs.getString("TaskName") + "'";
					int records=stmt1.executeUpdate(query1);
					if (records > 0) {
						createStatus = true;
					}
				}
				
			}
		} catch (SQLException sqlExcept) {
			throw sqlExcept;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	
	public static ArrayList<String> getTasksInGroup(String name) throws Exception {
		Connection con = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT TaskName,TaskPoints FROM tasks WHERE GroupName = '" + name + "' ";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				list.add(rs.getString("TaskName")+"----"+rs.getString("TaskPoints") +" "+ "points");
			}
		} catch (SQLException sqlExcept) {
			throw sqlExcept;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}
	
	
	public static boolean createTasksInGroup(String taskname, String taskpoints, String groupname,String isrecursive,String startdate)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "INSERT into tasks(TaskName,TaskPoints,GroupName,IsRecursive,StartDate) values('"
					+ taskname + "'," + "'" + taskpoints + "'," + "'" + groupname + "',"+ "'"+isrecursive+"',"+ "'"+startdate+"')";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcept) {
			throw sqlExcept;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static boolean assignTasksToUser(String taskname, String taskpoints, String email, String groupname, String currentdate)
			throws SQLException, Exception {
		boolean assignStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "INSERT into taskstopersonsmapping(TaskName,EmailId,GroupName, CurrentDate, TaskPoints) values('"
					+ taskname + "'," + "'" + email + "'," + "'" + groupname + "'," + "'" + currentdate + "'," + "'" + taskpoints + "')";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				assignStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return assignStatus;
	}
	
	public static ArrayList<String> getTasksAssignedInGroup(String name) throws Exception {
		Connection con = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT TaskName, EmailId, MarkCompletion, CurrentDate, TaskPoints FROM taskstopersonsmapping WHERE GroupName = '" + name + "' ";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				list.add(rs.getString("EmailId")+"*#*#*#*"+rs.getString("TaskName") + "~#~#~#~"+rs.getString("MarkCompletion")+ "^#^#^#^"+rs.getString("CurrentDate")+ "%#%#%#%"+rs.getString("TaskPoints"));
			}
		} catch (SQLException sqlExcept) {
			throw sqlExcept;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}
	
	public static ArrayList<String> getPersonsPointsInfo(String groupname, String selecteduser) throws Exception {
		Connection con = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "SELECT minPoints,PendingPoints,RecurrenceStartDate FROM groupstopersonsmapping WHERE EmailId = '"+selecteduser+"' AND GroupName = "+"'"+groupname+"'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				list.add(rs.getString("minPoints") + "*#*#*#*" + rs.getString("PendingPoints") + "~#~#~#~" + rs.getString("RecurrenceStartDate"));
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}
	
	public static boolean updateRecurrenceDate(String pendingPoints, String startRecurrence, String groupname, String selecteduser)
			throws Exception {
		boolean createStatus = false;
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			String query = "UPDATE groupstopersonsmapping SET PendingPoints = '"+pendingPoints+"', RecurrenceStartDate = '"+startRecurrence+"' WHERE  GroupName = '" + groupname + "' AND EmailId = '" + selecteduser + "'";
			int records = stmt.executeUpdate(query);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	public static boolean updatePendingPointsFromTasks(String taskPoints, String selecteduser, String groupname)
			throws Exception {
		boolean createStatus = false;
		String oldPendingPoints = "";
		Connection con = null;
		try {
			try {
				con = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt1 = con.createStatement();
			String query1 = "SELECT PendingPoints FROM groupstopersonsmapping WHERE GroupName = '" + groupname + "'AND EmailId = '" + selecteduser + "' ";
			ResultSet rs = stmt1.executeQuery(query1);
			while (rs.next()) { 
				oldPendingPoints = rs.getString("PendingPoints");
				oldPendingPoints = String.valueOf(Double.parseDouble(oldPendingPoints) - Double.parseDouble(taskPoints));
			}
			
			Statement stmt2 = con.createStatement();
			String query2 = "UPDATE groupstopersonsmapping SET PendingPoints = '"+oldPendingPoints+"' WHERE  GroupName = '" + groupname + "' AND EmailId = '" + selecteduser + "'";
			int records = stmt2.executeUpdate(query2);
			if (records > 0) {
				createStatus = true;
			}
		} catch (SQLException sqlExcep) {
			throw sqlExcep;
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return createStatus;
	}
	
	
}
