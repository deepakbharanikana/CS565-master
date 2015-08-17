package com.iubcsse.sharetask;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAssignedTasksActivity extends ActionBarActivity{
	TextView assignedTasks;
	String groupName,userName;
	protected static final String TASK_IDS = "taskIDs";
	protected static final String PERSON_IDS = "personIDs";
	protected static final String GROUP_NAME = "groupName";
	protected static final String USER_NAME = "userName";
	
	private ArrayList<String> mTaskIdsStringPending = new ArrayList<String>();
	private ArrayList<String> mTaskIdsStringCompleted = new ArrayList<String>(); 
	private ArrayList<String> mPersonsList = new ArrayList<String>();
	
	ActionBar tabBar;
	RequestParams param;
	TasksFragment pendingFrag = new TasksFragment();
	TasksFragment completedFrag = new TasksFragment();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assigned_task_frames);
		
		Bundle extras = getIntent().getExtras();
		groupName = extras.getString("groupname");
		userName = extras.getString("userName");
		param = new RequestParams();
		param.add("groupname",groupName);
		invokeWSForGettingAssignedTasksToPersons(param,false, false);
		
		// Put ActionBar in Tab Mode
		tabBar = getSupportActionBar();
		tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		// Store the list of tasks as an argument to the TaskFragment
		Bundle args = new Bundle();
		args.putStringArrayList(TASK_IDS, mTaskIdsStringPending);
		args.putString(GROUP_NAME, groupName);
		args.putString(USER_NAME, userName);
		args.putStringArrayList(PERSON_IDS, mPersonsList);
		pendingFrag.setArguments(args);
		// Configure a tab for the Pending task TaskFragment
		tabBar.addTab(tabBar.newTab().setText("Pending tasks")
						.setTabListener(new TabListener(pendingFrag)));
		
		
		// Store the list of tasks as an argument to the TaskFragment
		args = new Bundle();
		args.putStringArrayList(TASK_IDS, mTaskIdsStringCompleted);
		args.putString(GROUP_NAME, groupName);
		args.putString(USER_NAME, userName);
		args.putStringArrayList(PERSON_IDS, mPersonsList);
		completedFrag.setArguments(args);
		// Configure a tab for the Completed tasks TaskFragment
		tabBar.addTab(tabBar.newTab().setText("Completed tasks")
						.setTabListener(new TabListener(completedFrag)));
		
		
	}
	public void updateTab(boolean isComplete){
		invokeWSForGettingAssignedTasksToPersons(param, true,isComplete);
	}
	public void selectCompletedTab(boolean isComplete){
		if(isComplete){
			tabBar.setSelectedNavigationItem(0);
		}
	}
	
	public void selectPendingTab(boolean isComplete){
		if(isComplete){
			tabBar.setSelectedNavigationItem(1);
		}
	}
	
	public static class TabListener implements ActionBar.TabListener {
		private static final String TAG = "TabListener";
		private final Fragment mFragment;

		public TabListener(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		// When a tab is selected, change the currently visible Fragment
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabSelected called");

			if (null != mFragment) {
				ft.replace(R.id.fragment_container, mFragment);
			}
		}

		// When a tab is unselected, remove the currently visible Fragment
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabUnselected called");

			if (null != mFragment)
				ft.remove(mFragment);
		}
	}

	
	public void invokeWSForGettingAssignedTasksToPersons(RequestParams params, final boolean updated, final boolean isComplete) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/task/getAssignedTasks",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							mTaskIdsStringPending.clear();
							mTaskIdsStringCompleted.clear();
							mPersonsList.clear();
							if (obj.getBoolean("status")) {
								for (int i = 0; i < obj.getJSONArray("list")
										.length(); i++) {
									String temp = obj.getJSONArray("list").getString(i).toString();
									String Email = temp.substring(0, temp.indexOf("*#*#*#*"));
									String TaskName = temp.substring(temp.indexOf("*#*#*#*")+7, temp.indexOf("~#~#~#~"));
									String isCompleted = temp.substring(temp.indexOf("~#~#~#~")+7, temp.indexOf("^#^#^#^"));
									String dueDate = temp.substring(temp.indexOf("^#^#^#^")+7, temp.indexOf("%#%#%#%"));
									String taskPoints = temp.substring(temp.indexOf("%#%#%#%")+7, temp.length());
									if(isCompleted.equals("0")){
										mTaskIdsStringPending.add(Email+"*#*#*#*"+TaskName+ "~#~#~#~"+ isCompleted + "^#^#^#^" +dueDate+ "%#%#%#%" +taskPoints);
									}
									else{
										mTaskIdsStringCompleted.add(Email+"*#*#*#*"+TaskName +"~#~#~#~"+ isCompleted+ "^#^#^#^" +dueDate+ "%#%#%#%" +taskPoints);
									}
									mPersonsList.add(Email);
								}
								
								if(updated){
									if(isComplete){
										selectCompletedTab(false);
										selectPendingTab(true);
									}
									else{
										selectCompletedTab(true);
										selectPendingTab(false);
									}
									
								}
							} else {
								Toast.makeText(getApplicationContext(),
										obj.getString("error_msg"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(R.string.err_occured),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						if (statusCode == 404) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_resource_not_found),
									Toast.LENGTH_SHORT).show();
						}
						else if (statusCode == 500) {
							Toast.makeText(getApplicationContext(),
									getResources().getString(R.string.err_server_side), Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
	
}
