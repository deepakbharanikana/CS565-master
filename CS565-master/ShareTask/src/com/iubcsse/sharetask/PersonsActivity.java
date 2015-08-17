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

public class PersonsActivity extends ActionBarActivity {

	ListView personsList;
	List<String> your_array_list = new ArrayList<String>();
	CustomAdapter arrayAdapter;
	RequestParams paramsGroupName = new RequestParams();
	
	RequestParams paramsInfo = new RequestParams();
	String groupName;
	String userName;
	Button btnCreateTask, btnAddPersons, btnViewTasksAssigned, btnViewPendingPoints;
	static final int DATE_DIALOG_ID = 0;
	static final int DATE_DIALOG_TEST_ID = 1;
	
	String PendingPoints;
	String WeekPoints;
	String StartRecurrence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addpeople);

		Bundle extras = getIntent().getExtras();
		groupName= extras.getString("groupName");
		userName = extras.getString("userName");
		paramsGroupName.add("groupname", groupName);
		
		personsList = (ListView) findViewById(R.id.list_addPeople);
		btnCreateTask = (Button) findViewById(R.id.button_createTask);
		btnAddPersons = (Button) findViewById(R.id.button_addPersons);
		btnViewTasksAssigned = (Button) findViewById(R.id.button_viewTasksAssigned);
		btnViewPendingPoints = (Button) findViewById(R.id.button_viewPendingPoints);
		invokeWSForGettingPersons(paramsGroupName);
		
		Log.i("personActivity", "Retrives the groups from the database");
		
		arrayAdapter = new CustomAdapter(this, your_array_list);

		personsList.setAdapter(arrayAdapter);
		
		final Calendar c = Calendar.getInstance();
		mTestYear = c.get(Calendar.YEAR);
		mTestMonth = c.get(Calendar.MONTH);
		mTestDay = c.get(Calendar.DAY_OF_MONTH);
		updateTestDisplay();
		
		btnCreateTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CreateTask();
			}
		});
		
		btnViewTasksAssigned.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), ViewAssignedTasksActivity.class);
				intent.putExtra("groupname", groupName);
				intent.putExtra("userName", userName);
				startActivity(intent);
				
			}
		});
		
		btnViewPendingPoints.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), ViewPendingPointsActivity.class);
				intent.putExtra("groupname", groupName);
				startActivity(intent);
				
			}
		});
		
		btnAddPersons.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddPerson();
			}
		});
		
		Log.i("personActivity", "call to the add person function");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private int mTestYear;
	private int mTestMonth;
	private int mTestDay;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_testDate) {
			showDialog(DATE_DIALOG_TEST_ID);
			// Get the current date
			
			return true;
		}
		if (id == R.id.action_useCheck) {
			if(item.getTitle().equals("useAdvancedDate"))
			{
				item.setTitle("DisableAdvancedDate");
				((MyApplication) getApplicationContext()).setUseAdvancedDate(true);
			}
			else
			{
				item.setTitle("useAdvancedDate");
				((MyApplication) getApplicationContext()).setUseAdvancedDate(false);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private int mYear;
	private int mMonth;
	private int mDay;
	EditText startRecurrenceDate;
	
	private void AddPerson() {
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(PersonsActivity.this);

		alertDialog.setTitle(getResources().getString(R.string.alert_personTitle));
		
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setOrientation(LinearLayout.VERTICAL);

		final EditText inputPerson = new EditText(getApplicationContext());
		inputPerson.setHint("Person Name");
		inputPerson.setTextColor(getResources().getColor(R.color.black));
		layout.addView(inputPerson);
		
		final EditText weeklyPoints = new EditText(getApplicationContext());
		weeklyPoints.setHint("Weekly Recursive Points");
		weeklyPoints.setInputType(InputType.TYPE_CLASS_NUMBER);
		weeklyPoints.setTextColor(getResources().getColor(R.color.black));
		layout.addView(weeklyPoints);
		
		startRecurrenceDate = new EditText(getApplicationContext());
		startRecurrenceDate.setHint("Recurrence Start");
		startRecurrenceDate.setTextColor(getResources().getColor(R.color.black));
		layout.addView(startRecurrenceDate);
		startRecurrenceDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		
		// Get the current date
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);

				// Display the current date
				updateDisplay();
		
		alertDialog.setView(layout);
		
		final RequestParams params2 = new RequestParams();

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.key);

		alertDialog.setPositiveButton(getResources().getString(R.string.alert_createBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(inputPerson.getText().toString().length()!=0){
							params2.add("email", inputPerson.getText().toString());
							params2.add("groupname", groupName);
							params2.add("weeklyrecurvisepoints", weeklyPoints.getText().toString());
							params2.add("RecurrenceStart", startRecurrenceDate.getText().toString());
							invokeWSForPersonInsert(params2);
						}
					}
				});
        Log.i("personActivity", "the person gets added");
		alertDialog.setNegativeButton(getResources().getString(R.string.alert_cancelBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		Log.i("personActivity", "cancels the add");
        

		alertDialog.show();

	}
	
	// Update the date in the TextView
		private void updateDisplay() {
		
			startRecurrenceDate.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(mMonth + 1).append("-").append(mDay).append("-")
					.append(mYear).append(" "));
							
		}
		
		private void updateTestDisplay() {
			
				((MyApplication) getApplicationContext()).setTestSystemDate(new StringBuilder()
				.append(mTestMonth + 1).append("-").append(mTestDay).append("-")
				.append(mTestYear).append(" ").toString());
							
		}

		// Create and return DatePickerDialog
		@Override
		protected Dialog onCreateDialog(int id) {
			switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
						mDay);
			case DATE_DIALOG_TEST_ID:
				return new DatePickerDialog(this, mDateSetTestListener, mTestYear, mTestMonth,
						mTestDay);
			}
			return null;
		}

		private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				updateDisplay();
			}
		};
		
		private DatePickerDialog.OnDateSetListener mDateSetTestListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mTestYear = year;
				mTestMonth = monthOfYear;
				mTestDay = dayOfMonth;
				updateTestDisplay();
			}
		};
	
	public void invokeWSForPersonInsert(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/persons/insertpersons",params ,new AsyncHttpResponseHandler() {
             @Override
             public void onSuccess(String response) {
                 try {
                         JSONObject obj = new JSONObject(response);
                         if(obj.getBoolean("status")){
                             Toast.makeText(getApplicationContext(), getResources().getString(R.string.person_added), Toast.LENGTH_SHORT).show();
                             your_array_list.clear();
                             invokeWSForGettingPersons(paramsGroupName);
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
	
	private void CreateTask() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(PersonsActivity.this);

		alertDialog.setTitle(getResources().getString(R.string.alert_taskTitle));
		
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setOrientation(LinearLayout.VERTICAL);

		final EditText inputTask = new EditText(getApplicationContext());
		inputTask.setHint("Task Name");
		inputTask.setTextColor(getResources().getColor(R.color.black));
		layout.addView(inputTask);

		final EditText inputPoints = new EditText(getApplicationContext());
		inputPoints.setHint("Give Points");
		inputPoints.setInputType(InputType.TYPE_CLASS_PHONE);
		inputPoints.setTextColor(getResources().getColor(R.color.black));
		layout.addView(inputPoints);
		
		final CheckBox isRecursive = new CheckBox(getApplicationContext());
		isRecursive.setText("Is this a weekly recursive task ?");
		isRecursive.setTextColor(getResources().getColor(R.color.black));
		layout.addView(isRecursive);

		alertDialog.setView(layout);
		
		Log.i("personActivity", "Creates the layout to add a new task allows to select if the task is reccuring task");
		
		final RequestParams params2 = new RequestParams();

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.key);

		alertDialog.setPositiveButton(getResources().getString(R.string.alert_createBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(inputTask.getText().toString().length()!=0 && inputPoints.getText().length()!=0 ){
							params2.add("taskname", inputTask.getText().toString());
							params2.add("taskpoints", inputPoints.getText().toString());
							if(isRecursive.isChecked()){
								params2.add("isrecursive","1");
							}
							else params2.add("isrecursive", "0");
							params2.add("groupname", groupName);
							Calendar c = Calendar.getInstance();
							SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
							params2.add("startdate",df.format(c.getTime()));
							invokeWSForTaskInsert(params2);
							Log.i("personActivity", "Invokes the web service to insert task into the database based on the added parameters");
						}
						else
						{
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_empty_fields), Toast.LENGTH_SHORT).show();
						}
					}
				});

		alertDialog.setNegativeButton(getResources().getString(R.string.alert_cancelBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();

	}
	
	public void invokeWSForTaskInsert(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/task/inserttasks",params ,new AsyncHttpResponseHandler() {
             @Override
             public void onSuccess(String response) {
                 try {
                         JSONObject obj = new JSONObject(response);
                         if(obj.getBoolean("status")){
                             Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_added), Toast.LENGTH_SHORT).show();
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
                     Toast.makeText(getApplicationContext(),  getResources().getString(R.string.err_resource_not_found), Toast.LENGTH_SHORT).show();
                 } 
                 else if(statusCode == 500){
                     Toast.makeText(getApplicationContext(),  getResources().getString(R.string.err_server_side), Toast.LENGTH_SHORT).show();
                 } 
                 else{
                     Toast.makeText(getApplicationContext(),  getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
                 }
             }
         });
        Log.i("personActivity", "Does some field validations");
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
			
			convertView = inflater.inflate(R.layout.person_list_item, null);
			}
			final TextView textUserName = (TextView)convertView.findViewById(R.id.personName);
			
			final EditText editWeeklyPoints = (EditText)convertView.findViewById(R.id.minPoints);
			
			final Button editBtn = (Button) convertView.findViewById(R.id.edit);
			String listText = mThumbIds.get(position);
			final String emailId = listText.substring(0, listText.indexOf("*#*#*#*"));
			textUserName.setText(emailId);
			WeekPoints = (listText.substring(listText.indexOf("*#*#*#*")+7,listText.indexOf("~#~#~#~")));
			editWeeklyPoints.setText(WeekPoints);
			final RequestParams params2 = new RequestParams();
			
			editBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(editBtn.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_edit).getConstantState())){
						editWeeklyPoints.setEnabled(true);
						editWeeklyPoints.requestFocus();
						editWeeklyPoints.setSelection(editWeeklyPoints.getText().length());
						editBtn.setBackgroundResource(R.drawable.ic_action_accept);
					}
					else if(editBtn.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_accept).getConstantState())){
						editBtn.setBackgroundResource(R.drawable.ic_action_edit);
						editWeeklyPoints.setEnabled(false);
						params2.add("email", emailId);
						params2.add("groupname", groupName);
						params2.add("minpoints",editWeeklyPoints.getText().toString());
						
						invokeWSForPersonMinPoints(params2);
					}
					
				}
			});
			
			textUserName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String selectedPersonName = textUserName.getText().toString();
					Intent intent = new Intent(getApplicationContext(),AssignTaskActivity.class);
					intent.putExtra("personName", selectedPersonName);
					intent.putExtra("groupName", groupName);
					
					startActivity(intent);
				}
			});
			
			return convertView;
		}
		
	}

public void invokeWSForPersonMinPoints(RequestParams params){
    AsyncHttpClient client = new AsyncHttpClient();
    client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/persons/insertminpoints",params ,new AsyncHttpResponseHandler() {
         @Override
         public void onSuccess(String response) {
             try {
                     JSONObject obj = new JSONObject(response);
                     if(obj.getBoolean("status")){
                         your_array_list.clear();
                         invokeWSForGettingPersons(paramsGroupName);
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
    Log.i("personActivity", "Does validation on minimum points for each task");
}

}
