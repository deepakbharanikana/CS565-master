package com.sharetask.webservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/persons")
public class Persons {
    @GET
    @Path("/getPersons")
    @Produces(MediaType.APPLICATION_JSON) 
    public String getPersons(@QueryParam("groupname") String name){
        String response = "";
        ArrayList<String> arr = null;
		try {
			arr = DBConnection.getPersonsInGroup(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if(arr!=null){
            response = Utility.constructJSON("getPersons",true,arr);
        }else{
            response = Utility.constructJSON("getPersons", false, "No Persons in group");
        }
    return response;        
    }
    
    @GET
    @Path("/insertpersons")  
    @Produces(MediaType.APPLICATION_JSON) 
    public String doInsert(@QueryParam("email") String email, @QueryParam("groupname") String groupname, @QueryParam("weeklyrecurvisepoints") String weeklyrecurvisepoints, @QueryParam("RecurrenceStart") String RecurrenceStart){
        String response = "";
        int retCode = insertPersonsInGroup(email, groupname, weeklyrecurvisepoints, RecurrenceStart);
        if(retCode == 0){
            response = Utility.constructJSON("insertpersons",true);
        }else if(retCode == 1){
            response = Utility.constructJSON("insertpersons",false, "Person already in group");
        }else if(retCode == 2){
            response = Utility.constructJSON("insertpersons",false, "Special Characters are not allowe");
        }else if(retCode == 3){
            response = Utility.constructJSON("insertpersons",false, "Error occured");
        }
        return response;
 
    }
 
    private int insertPersonsInGroup(String email, String groupname, String weeklyrecurvisepoints, String RecurrenceStart){
        int result = 3;
            try {
                if(DBConnection.createPersonsInGroup(email, groupname, weeklyrecurvisepoints, RecurrenceStart)){
                    result = 0;
                }
            } catch(SQLException sqle){
                //When Primary key violation occurs
                if(sqle.getErrorCode() == 1062){
                    result = 1;
                } 
                else if(sqle.getErrorCode() == 1064){
                    result = 2;
                }
            }
            catch (Exception e) {
                result = 3;
            }
 
        return result;
    }
    
    @GET
    @Path("/insertminpoints")  
    @Produces(MediaType.APPLICATION_JSON) 
    public String doInsertMinPoints(@QueryParam("email") String email, @QueryParam("groupname") String groupname, @QueryParam("minpoints") String minpoints){
        String response = "";
        int retCode = insertPersonsMinPoints(email, groupname, minpoints);
        if(retCode == 0){
            response = Utility.constructJSON("insertpersons",true);
        }else if(retCode == 1){
            response = Utility.constructJSON("insertpersons",false, "Person already in group");
        }else if(retCode == 2){
            response = Utility.constructJSON("insertpersons",false, "Special Characters are not allowe");
        }else if(retCode == 3){
            response = Utility.constructJSON("insertpersons",false, "Error occured");
        }
        return response;
 
    }
    
    private int insertPersonsMinPoints(String email, String groupname, String minpoints){
        int result = 3;
            try {
                if(DBConnection.createPersonsMinPoints(email, groupname, minpoints)){
                    result = 0;
                }
            } catch(SQLException sqle){
                //When Primary key violation occurs
                if(sqle.getErrorCode() == 1062){
                    result = 1;
                } 
                else if(sqle.getErrorCode() == 1064){
                    result = 2;
                }
            }
            catch (Exception e) {
                result = 3;
            }
 
        return result;
    }
    
    @GET
    @Path("/getPersonsPointsInfo")
    @Produces(MediaType.APPLICATION_JSON) 
    public String getPersonsInfo(@QueryParam("groupname") String groupname, @QueryParam("selecteduser") String selecteduser){
        String response = "";
        ArrayList<String> arr = null;
		try {
			arr = DBConnection.getPersonsPointsInfo(groupname, selecteduser);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if(arr!=null){
            response = Utility.constructJSON("getPersons",true,arr);
        }else{
            response = Utility.constructJSON("getPersons", false, "Error retrieving Points");
        }
    return response;        
    }
    
    @GET
    @Path("/doInsertRecurrenceDate")  
    @Produces(MediaType.APPLICATION_JSON) 
    public String doInsertRecurrenceDate(@QueryParam("pendingPoints") String pendingPoints, @QueryParam("startRecurrence") String startRecurrence, @QueryParam("groupname") String groupname, @QueryParam("selecteduser") String selecteduser){
        String response = "";
        int retCode = insertPersonsRecurrenceDate(pendingPoints, startRecurrence,groupname,selecteduser);
        if(retCode == 0){
            response = Utility.constructJSON("insertpersons",true);
        }else if(retCode == 1){
            response = Utility.constructJSON("insertpersons",false, "Person already in group");
        }else if(retCode == 2){
            response = Utility.constructJSON("insertpersons",false, "Special Characters are not allowe");
        }else if(retCode == 3){
            response = Utility.constructJSON("insertpersons",false, "Error occured");
        }
        return response;
 
    }
    
    private int insertPersonsRecurrenceDate(String pendingPoints, String startRecurrence,String groupname, String selecteduser){
        int result = 3;
            try {
                if(DBConnection.updateRecurrenceDate(pendingPoints, startRecurrence,groupname,selecteduser)){
                    result = 0;
                }
            } catch(SQLException sqle){
                //When Primary key violation occurs
                if(sqle.getErrorCode() == 1062){
                    result = 1;
                } 
                else if(sqle.getErrorCode() == 1064){
                    result = 2;
                }
            }
            catch (Exception e) {
                result = 3;
            }
 
        return result;
    }
    
    @GET
    @Path("/doModifyPendingPoints")  
    @Produces(MediaType.APPLICATION_JSON) 
    public String doModifyPendingPoints(@QueryParam("taskPoints") String taskPoints, @QueryParam("email") String selecteduser, @QueryParam("groupname") String groupname){
        String response = "";
        int retCode = ModifyPendingPoints(taskPoints,selecteduser, groupname);
        if(retCode == 0){
            response = Utility.constructJSON("insertpersons",true);
        }else if(retCode == 1){
            response = Utility.constructJSON("insertpersons",false, "Person already in group");
        }else if(retCode == 2){
            response = Utility.constructJSON("insertpersons",false, "Special Characters are not allowe");
        }else if(retCode == 3){
            response = Utility.constructJSON("insertpersons",false, "Error occured");
        }
        return response;
 
    }
    
    private int ModifyPendingPoints(String taskPoints, String selecteduser,String groupname){
        int result = 3;
            try {
                if(DBConnection.updatePendingPointsFromTasks(taskPoints, selecteduser,groupname)){
                    result = 0;
                }
            } catch(SQLException sqle){
                //When Primary key violation occurs
                if(sqle.getErrorCode() == 1062){
                    result = 1;
                } 
                else if(sqle.getErrorCode() == 1064){
                    result = 2;
                }
            }
            catch (Exception e) {
                result = 3;
            }
 
        return result;
    }
}
