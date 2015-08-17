package com.iubcsse.sharetask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TasksFragment extends Fragment {
	
	private List<String> mTaskIDs;
	private List<String> mPersonIDs;
	private ListView mListView;
	private TextView mPendingPoints;
	List<String> your_array_list = new ArrayList<String>();
	private String groupname,username;
	List<String> list;
	String selectedUser;
	Spinner s ;
	String PendingPoints;
	String WeekPoints;
	String StartRecurrence;
    ArrayAdapter<String> dataAdapter;
    private List<String> mTaskPerUser= new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTaskIDs = getArguments().getStringArrayList(ViewAssignedTasksActivity.TASK_IDS);
		mPersonIDs = getArguments().getStringArrayList(ViewAssignedTasksActivity.PERSON_IDS);
		groupname = getArguments().getString(ViewAssignedTasksActivity.GROUP_NAME);
		username  = getArguments().getString(ViewAssignedTasksActivity.USER_NAME);
		
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mPersonIDs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(dataAdapter);
		selectedUser=username;
		if(mPersonIDs.contains(username)){
			s.setSelection(mPersonIDs.indexOf(username));
		}
		
		//mListView.setAdapter(new CustomAdapter(getActivity(), mTaskIDs));
	}
	private Context mContext;
	private class CustomAdapter extends BaseAdapter{
		
		
		private List<String> mTaskIDs;

		public CustomAdapter(Activity activity, List<String> TaskIDs) {
			mTaskIDs = TaskIDs;
			mContext = activity;
		}

		@Override
		public int getCount() {
			return mTaskIDs.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.activity_assigned_tasks, null);
			}
			final TextView textDueDate = (TextView)convertView.findViewById(R.id.dueDate);
			final TextView textTaskName = (TextView)convertView.findViewById(R.id.taskName);
			final TextView textTaskPoints = (TextView)convertView.findViewById(R.id.taskPoints);
			
			final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
			
			String temp = mTaskIDs.get(position);
			final String Email = temp.substring(0, temp.indexOf("*#*#*#*"));
			final String TaskName = temp.substring(temp.indexOf("*#*#*#*")+7, temp.indexOf("~#~#~#~"));
			String isCompleted = temp.substring(temp.indexOf("~#~#~#~")+7, temp.indexOf("^#^#^#^"));
			String dueDate = temp.substring(temp.indexOf("^#^#^#^")+7, temp.indexOf("%#%#%#%"));
			final String taskPoints = temp.substring(temp.indexOf("%#%#%#%")+7, temp.length());
			
			final RequestParams params2 = new RequestParams();
			
			textDueDate.setText(dueDate);
			textTaskName.setText(TaskName);
			textTaskPoints.setText(taskPoints);
			

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			String currentDate;
			if(((MyApplication) mContext.getApplicationContext()).getUseAdvancedDate()){
				currentDate = ((MyApplication) mContext.getApplicationContext()).getTestSystemDate();
			}
			else
				currentDate = sdf.format(c.getTime());

			Date date1 = new Date();
			try {
				date1 = sdf.parse(currentDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Date date2 = new Date();
			try {
				date2 = sdf.parse(dueDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(isCompleted.equals("0")){
				check.setChecked(false);
				if (date2.before(date1)) {
					convertView.setBackgroundColor(getResources().getColor(R.color.red));
				}
			}
			else
				check.setChecked(true);
			
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					params2.add("email", Email);
					params2.add("taskname", TaskName);
					params2.add("groupname",groupname);
					RequestParams pointsParams = new RequestParams();
					pointsParams.add("groupname",groupname);
					pointsParams.add("email", Email);
					if(check.isChecked())
					{
						params2.add("iscompleted","1");
						pointsParams.add("taskPoints",taskPoints);
						invokeWSForUpdatePendingPoints(pointsParams, mContext);
					}
					else{
						params2.add("iscompleted","0");
						pointsParams.add("taskPoints","-"+taskPoints);
						invokeWSForUpdatePendingPoints(pointsParams, mContext);
					}
					invokeWSForUpdateTaskCompletion(params2, mContext, check.isChecked());
				}
			});
			
			return convertView;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.task_fragment_main, container, false);
		s = (Spinner) view.findViewById(R.id.spinner_taskList);
		mListView = (ListView) view.findViewById(R.id.listview); 
		mPendingPoints = (TextView) view.findViewById(R.id.text_pendingPoints);
	     
	        
	     s.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					selectedUser = s.getItemAtPosition(arg2).toString();
					mTaskPerUser.clear();

					for(int i=0;i<mTaskIDs.size();i++){
						if(mTaskIDs.get(i).contains(selectedUser))
						{
							mTaskPerUser.add(mTaskIDs.get(i));
							
						}
					}
					mListView.setAdapter(new CustomAdapter(getActivity(), mTaskPerUser));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}

			});
	        
		return view;	
	}
	
	public void invokeWSForUpdateTaskCompletion(RequestParams params, final Context context, final boolean isChecked){
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/task/updatetaskcompletion",params ,new AsyncHttpResponseHandler() {
	         @Override
	         public void onSuccess(String response) {
	             try {
	                     JSONObject obj = new JSONObject(response);
	                     if(obj.getBoolean("status")){
	                    	if(isChecked){
	                    		((ViewAssignedTasksActivity)getActivity()).updateTab(true);
	                    	}
	                    	else
	                    		((ViewAssignedTasksActivity)getActivity()).updateTab(false);
	                     } 
	                     else{
	                         Toast.makeText(context, obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
	                     }
	             } catch (JSONException e) {
	                 Toast.makeText(context, getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
	                 e.printStackTrace();

	             }
	         }
	         @Override
	         public void onFailure(int statusCode, Throwable error,
	             String content) {
	             if(statusCode == 404){
	                 Toast.makeText(context, getResources().getString(R.string.err_resource_not_found), Toast.LENGTH_SHORT).show();
	             } 
	             else if(statusCode == 500){
	                 Toast.makeText(context, getResources().getString(R.string.err_server_side), Toast.LENGTH_SHORT).show();
	             } 
	             else{
	                 Toast.makeText(context, getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
	             }
	         }
	     });
	}
	
	public void invokeWSForUpdatePendingPoints(RequestParams params, final Context context){
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/persons/doModifyPendingPoints",params ,new AsyncHttpResponseHandler() {
	         @Override
	         public void onSuccess(String response) {
	             try {
	                     JSONObject obj = new JSONObject(response);
	                     if(obj.getBoolean("status")){
	                     } 
	                     else{
	                         Toast.makeText(context, obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
	                     }
	             } catch (JSONException e) {
	                 Toast.makeText(context, getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
	                 e.printStackTrace();

	             }
	         }
	         @Override
	         public void onFailure(int statusCode, Throwable error,
	             String content) {
	             if(statusCode == 404){
	                 Toast.makeText(context, getResources().getString(R.string.err_resource_not_found), Toast.LENGTH_SHORT).show();
	             } 
	             else if(statusCode == 500){
	                 Toast.makeText(context, getResources().getString(R.string.err_server_side), Toast.LENGTH_SHORT).show();
	             } 
	             else{
	                 Toast.makeText(context, getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
	             }
	         }
	     });
	}
	
}
