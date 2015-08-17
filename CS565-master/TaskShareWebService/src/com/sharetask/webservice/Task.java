package com.sharetask.webservice;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/task")
public class Task {
	 @GET
	    @Path("/getTasks")
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String getTasks(@QueryParam("groupname") String name){
	        String response = "";
	        ArrayList<String> arr = null;
			try {
				arr = DBConnection.getTasksInGroup(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(arr!=null){
	            response = Utility.constructJSON("getTasks",true,arr);
	        }else{
	            response = Utility.constructJSON("getTasks", false, "No Tasks");
	        }
	    return response;        
	    }
	 
	 @GET
	    @Path("/inserttasks")  
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String doInsert(@QueryParam("taskname") String taskname, @QueryParam("taskpoints") String taskpoints, @QueryParam("groupname") String groupname, @QueryParam("isrecursive") String isrecursive, @QueryParam("startdate") String startdate){
	        String response = "";
	        int retCode = createTasksInGroup(taskname, taskpoints, groupname, isrecursive,startdate);
	        if(retCode == 0){
	            response = Utility.constructJSON("inserttasks",true);
	        }else if(retCode == 1){
	            response = Utility.constructJSON("inserttasks",false, "Task already present");
	        }else if(retCode == 2){
	            response = Utility.constructJSON("inserttasks",false, "Special Characters are not allowed");
	        }else if(retCode == 3){
	            response = Utility.constructJSON("inserttasks",false, "Error occured");
	        }
	        return response;
	 
	    }
	 
	    private int createTasksInGroup(String taskname, String taskpoints, String groupname, String isrecursive,String startdate){
	        int result = 3;
	            try {
	                if(DBConnection.createTasksInGroup(taskname,taskpoints, groupname, isrecursive, startdate)){
	                    result = 0;
	                }
	            } catch(SQLException sqle){
	                if(sqle.getErrorCode() == 1062){
	                    result = 1;
	                } 
	                else if(sqle.getErrorCode() == 1064){
	                    System.out.println(sqle.getErrorCode());
	                    result = 2;
	                }
	            }
	            catch (Exception e) {
	                result = 3;
	            }
	 
	        return result;
	    }
	    
	    
	    @GET
	    @Path("/assigntasks")  
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String doInsertTask(@QueryParam("taskname") String taskname,@QueryParam("taskpoints") String taskpoints, @QueryParam("email") String email, @QueryParam("groupname") String groupname, @QueryParam("currentdate") String currentdate){
	        String response = "";
	        int retCode = assignTasksToUser(taskname,taskpoints, email, groupname, currentdate);
	        if(retCode == 0){
	            response = Utility.constructJSON("assigntasks",true);
	        }else if(retCode == 1){
	            response = Utility.constructJSON("assigntasks",false, "Task already assigned");
	        }else if(retCode == 2){
	            response = Utility.constructJSON("assigntasks",false, "Special Characters are not allowed in Username and Password");
	        }else if(retCode == 3){
	            response = Utility.constructJSON("assigntasks",false, "Error occured");
	        }
	        return response;
	 
	    }
	 
	    private int assignTasksToUser(String taskname, String taskpoints, String email, String groupname, String currentdate){
	        int result = 3;
	            try {
	                if(DBConnection.assignTasksToUser(taskname,taskpoints,email, groupname, currentdate)){
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
	 
	        return result;
	    }
	    
	    @GET
	    @Path("/getAssignedTasks")
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String getAssignedTasks(@QueryParam("groupname") String name){
	        String response = "";
	        ArrayList<String> arr = null;
			try {
				arr = DBConnection.getTasksAssignedInGroup(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(arr!=null){
	            response = Utility.constructJSON("getAssignedTasks",true,arr);
	        }else{
	            response = Utility.constructJSON("getAssignedTasks", false, "No Assigned Tasks");
	        }
	    return response;        
	    }
	    
	    @GET
	    @Path("/updatetaskcompletion")  
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String doUpdateTaskCompletion(@QueryParam("email") String email, @QueryParam("taskname") String taskname, @QueryParam("iscompleted") String iscompleted,  @QueryParam("groupname") String groupname){
	        String response = "";
	        int retCode = UpdateTaskCompletion(email, taskname, iscompleted, groupname);
	        if(retCode == 0){
	            response = Utility.constructJSON("updatetaskcompletion",true);
	        }else if(retCode == 1){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Update Failed");
	        }else if(retCode == 2){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Special Characters are not allowe");
	        }else if(retCode == 3){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Error occured");
	        }
	        return response;
	 
	    }
	    
	    private int UpdateTaskCompletion(String email, String taskname, String iscompleted, String groupname){
	        int result = 3;
	            try {
	                if(DBConnection.updateTaskCompletion(email, taskname, iscompleted, groupname)){
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
	    @Path("/autoadjusttaskpoints")  
	    @Produces(MediaType.APPLICATION_JSON) 
	    public String doAutoAdjustTaskPoints(@QueryParam("groupname") String groupname, @QueryParam("selecteTaskName") String selecteTaskName, @QueryParam("totalDelta") String totalDelta,  @QueryParam("totalTaskPoints") String totalTaskPoints,  @QueryParam("selectedTaskPoints") String selectedTaskPoints){
	        String response = "";
	        int retCode = AutoAdjustTaskPoints(groupname, selecteTaskName, totalDelta, totalTaskPoints, selectedTaskPoints);
	        if(retCode == 0){
	            response = Utility.constructJSON("updatetaskcompletion",true);
	        }else if(retCode == 1){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Update Failed");
	        }else if(retCode == 2){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Special Characters are not allowe");
	        }else if(retCode == 3){
	            response = Utility.constructJSON("updatetaskcompletion",false, "Error occured");
	        }
	        return response;
	 
	    }
	    
	    private int AutoAdjustTaskPoints(String groupname, String selecteTaskName, String totalDelta, String totalTaskPoints, String selectedTaskPoints){
	        int result = 3;
	            try {
	                if(DBConnection.autoAdjustPoints(groupname, selecteTaskName, totalDelta, totalTaskPoints, selectedTaskPoints)){
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
