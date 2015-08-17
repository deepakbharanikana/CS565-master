package com.sharetask.webservice;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/register")
public class Register {

	@GET
    @Path("/doRegister")  
    @Produces(MediaType.APPLICATION_JSON) 
    public String doRegister(@QueryParam("name") String name, @QueryParam("username") String uname, @QueryParam("password") String pwd){
        String response = "";
        int retCode = registerUser(name, uname, pwd);
        if(retCode == 0){
            response = Utility.constructJSON("register",true);
        }else if(retCode == 1){
            response = Utility.constructJSON("register",false, "You are already registered");
        }else if(retCode == 2){
            response = Utility.constructJSON("register",false, "Special Characters are not allowed");
        }else if(retCode == 3){
            response = Utility.constructJSON("register",false, "Error occured");
        }
        return response;
	}
	
	private int registerUser(String name, String uname, String pwd){
        int result = 3;
        if(uname.length()>0 && pwd.length()>0){
            try {
                if(DBConnection.createUser(name, uname, pwd)){
                    result = 0;
                }
            } catch(SQLException sqle){
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
        }else{
            result = 3;
        }
        return result;
    }
}
