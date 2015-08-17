package com.sharetask.webservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/groups")
public class Groups {
	@GET
	@Path("/getGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroups(@QueryParam("username") String emailId){
        String response = "";
        ArrayList<String> arr = null;
		try {
			arr = DBConnection.getUserGroups(emailId);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if(arr!=null){
            response = Utility.constructJSON("getGroups",true,arr);
        }else{
        	response = Utility.constructJSON("getGroups", false, "No Available Groups");
        }
    return response;        
    }
	
	@GET
    @Path("/insertgroups")  
    @Produces(MediaType.APPLICATION_JSON) 
	public String doInsert(@QueryParam("Gname") String name, @QueryParam("email") String email){
	        String response = "";
	        int retCode = createGroup(name,email);
	        if(retCode == 0){
	            response = Utility.constructJSON("createGroup",true);
	        }else if(retCode == 1){
	            response = Utility.constructJSON("createGroup",false, "Group already exist");
	        }else if(retCode == 2){
	            response = Utility.constructJSON("createGroup",false, "Special Characters are not allowed");
	        }else if(retCode == 3){
	            response = Utility.constructJSON("createGroup",false, "Error occured");
	        }
	        return response;
	 
	}
	 
	    private int createGroup(String name, String email){
	        System.out.println("Inside checkCredentials");
	        int result = 3;
	            try {
	                if(DBConnection.createGroup(name,email)){
	                    System.out.println("RegisterUSer if");
	                    result = 0;
	                }
	            } catch(SQLException sqle){
	                System.out.println("RegisterUSer catch sqle");
	                if(sqle.getErrorCode() == 1062){
	                    result = 1;
	                } 
	                else if(sqle.getErrorCode() == 1064){
	                    System.out.println(sqle.getErrorCode());
	                    result = 2;
	                }
	            }
	            catch (Exception e) {
	                System.out.println("Invalid ");
	                result = 3;
	            }
	 
	        return result;
	    }
    
}
