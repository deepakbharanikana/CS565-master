package com.sharetask.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class Login {
	@GET
	@Path("/loginAction")
	@Produces(MediaType.APPLICATION_JSON)
	public String loginAction(@QueryParam("username") String uname, @QueryParam("password") String password){
		String response = "";
		if(checkCredentials(uname, password)){
			response = Utility.constructJSON("login",true);
		}
		else{
            response = Utility.constructJSON("login", false, "Incorrect Email or Password");
        }
		return response;  
	}
	
	 private boolean checkCredentials(String uname, String password){
	        boolean isSuccess = false;
	        if(uname.length()>0 && password.length()>0){
	            try {
	            	isSuccess = DBConnection.userAuthentication(uname, password);
	            } catch (Exception e) {
	            	isSuccess = false;
	            }
	        }else{
	        	isSuccess = false;
	        }
	        return isSuccess;
	    }
}
