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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ViewPendingPointsActivity extends ActionBarActivity {

	ListView personsList;
	List<String> your_array_list = new ArrayList<String>();
	CustomAdapter arrayAdapter;
	RequestParams paramsGroupName = new RequestParams();
	String groupName;
	String PendingPoints;
	String WeekPoints;
	String StartRecurrence;
	
	TextView textUserName;
	TextView textPendingPoints;
    TextView editWeeklyPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pending_points);
		Bundle extras = getIntent().getExtras();
		groupName= extras.getString("groupname");
		paramsGroupName.add("groupname", groupName);
		personsList = (ListView) findViewById(R.id.list_addPeople);
		invokeWSForGettingPersons(paramsGroupName);
		arrayAdapter = new CustomAdapter(this, your_array_list);
		personsList.setAdapter(arrayAdapter);
		
	}
	
	public void dateCheck(String emailId){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String currentDate;
		
		if(((MyApplication) getApplicationContext()).getUseAdvancedDate()){
			currentDate = ((MyApplication) getApplicationContext()).getTestSystemDate();
		}
		else
			currentDate = sdf.format(c.getTime());

		Date dateCurrent = new Date();
		try {
			dateCurrent = sdf.parse(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date dateRecurrence = new Date();
		try {
			dateRecurrence = sdf.parse(StartRecurrence);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
			if (dateCurrent.after(dateRecurrence) ||dateCurrent.equals(dateRecurrence)) {
				long diff = dateCurrent.getTime() - dateRecurrence.getTime();
				long days =TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				long weeks = days/7;
				PendingPoints =String.valueOf(Double.parseDouble(PendingPoints) + (weeks+1)*Double.parseDouble(WeekPoints));
				
				Calendar cal  = Calendar.getInstance();
				cal.setTime(dateRecurrence);
				cal.add(Calendar.DATE, (int) ((weeks+1)*7));
				StartRecurrence = sdf.format(cal.getTime()); 
				
				RequestParams param = new RequestParams();
				param.add("pendingPoints", PendingPoints);
				param.add("startRecurrence", StartRecurrence);
				param.add("groupname",groupName);
				param.add("selecteduser",emailId);
				
				editWeeklyPoints.setText(WeekPoints);
				textPendingPoints.setText(PendingPoints);
				
				invokeWSForModifyRecurrenceDate(param);
			}
	}
	
	public void invokeWSForGettingPersons(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/persons/getPersons",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							if (obj.getBoolean("status")) {
								for (int i = 0; i < obj.getJSONArray("list")
										.length(); i++) {
									your_array_list.add(obj
											.getJSONArray("list").getString(i)
											.toString());
									arrayAdapter.notifyDataSetChanged();
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

	
private class CustomAdapter extends BaseAdapter{
		
		private Context mContext;
		private List<String> mThumbIds;

		public CustomAdapter(Activity activity, List<String> mThumbNailIDs) {
			mThumbIds = mThumbNailIDs;
			mContext = activity;
		}

		@Override
		public int getCount() {
			return mThumbIds.size();
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
			
			convertView = inflater.inflate(R.layout.pending_points_list_item, null);
			}
			textUserName = (TextView)convertView.findViewById(R.id.personName);
			textPendingPoints = (TextView)convertView.findViewById(R.id.pendingPoints);
			editWeeklyPoints = (TextView)convertView.findViewById(R.id.minPoints);
			
			String listText = mThumbIds.get(position);
			final String emailId = listText.substring(0, listText.indexOf("*#*#*#*"));
			textUserName.setText(emailId);
			WeekPoints = (listText.substring(listText.indexOf("*#*#*#*")+7,listText.indexOf("~#~#~#~")));
			PendingPoints = (listText.substring(listText.indexOf("~#~#~#~")+7,listText.indexOf("%#%#%#%")));
			StartRecurrence = (listText.substring(listText.indexOf("%#%#%#%")+7,listText.length()));
			editWeeklyPoints.setText(WeekPoints);
			textPendingPoints.setText(PendingPoints);
			dateCheck(emailId);
			return convertView;
		}
		
	}


public void invokeWSForModifyRecurrenceDate(RequestParams params){
    AsyncHttpClient client = new AsyncHttpClient();
    client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/persons/doInsertRecurrenceDate",params ,new AsyncHttpResponseHandler() {
         @Override
         public void onSuccess(String response) {
             try {
                     JSONObject obj = new JSONObject(response);
                     if(obj.getBoolean("status")){
                    	 //invokeWSForGettingPersons(paramsGroupName);
                     } 
                     else{
                         Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                     }
             } catch (JSONException e) {
                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
                 e.printStackTrace();

             }
         }
         @Override
         public void onFailure(int statusCode, Throwable error,
             String content) {
             if(statusCode == 404){
                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_resource_not_found), Toast.LENGTH_SHORT).show();
             } 
             else if(statusCode == 500){
                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_server_side), Toast.LENGTH_SHORT).show();
             } 
             else{
                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
             }
         }
     });
}
	
}
