package com.iubcsse.sharetask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AssignTaskActivity extends ActionBarActivity {

	List<String> your_array_list;
	ArrayAdapter<String> arrayAdapter;
	RequestParams paramsGroupName;
	String personName, groupName;
	Button btnAssignTask;
	Spinner taskList;
	TextView labelUserName;
	String selectedTask;
	String selectedTaskPoints;
	Double totalDelta;

	private TextView mDateDisplay;
	private Button mPickDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	RequestParams params2 = new RequestParams();
	Double totalTaskPoints=0.0;
	
	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assign_task);

		Bundle extras = getIntent().getExtras();
		personName = extras.getString("personName");
		groupName = extras.getString("groupName");
		your_array_list = new ArrayList<String>();
		paramsGroupName = new RequestParams();
		paramsGroupName.add("groupname", groupName);

		btnAssignTask = (Button) findViewById(R.id.button_assignTask);
		taskList = (Spinner) findViewById(R.id.spinner_taskList);
		labelUserName = (TextView) findViewById(R.id.label_username);

		labelUserName.setText(getResources().getString(
				R.string.text_assignTask, personName));
		invokeWSForGettingTasks(paramsGroupName);

		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, your_array_list);

		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		taskList.setAdapter(arrayAdapter);

		final RequestParams params = new RequestParams();

		mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
		mPickDate = (Button) findViewById(R.id.pickDate);

		// Set an OnClickListener on the Change The Date Button
		mPickDate.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
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

		btnAssignTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				params.add("email", personName);
				params.add("groupname", groupName);
				params.add("taskname", selectedTask);
				params.add("taskpoints", selectedTaskPoints);
				params.add("currentdate", mDateDisplay.getText().toString());
				invokeWSForTaskAssignment(params);
			}
		});
		
		taskList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
					String tempTaskNamePoints = taskList.getItemAtPosition(arg2).toString();
					selectedTask = tempTaskNamePoints.substring(0, tempTaskNamePoints.indexOf("----"));
					selectedTaskPoints = tempTaskNamePoints.substring(tempTaskNamePoints.indexOf("----")+4, tempTaskNamePoints.indexOf("points")-1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
	}

	public void invokeWSForUpdateTaskCompletion(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://" + Utility.localhost
				+ "/TaskShareWebService/restful/task/autoadjusttaskpoints",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							if (obj.getBoolean("status")) {
								// TODO
							} else {
								Toast.makeText(getApplicationContext(),
										obj.getString("error_msg"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						if (statusCode == 404) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_resource_not_found),
									Toast.LENGTH_SHORT).show();
						} else if (statusCode == 500) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_server_side),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	// Update the date in the TextView
	private void updateDisplay() {
		mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear).append(" "));
	}

	// Create and return DatePickerDialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void invokeWSForGettingTasks(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://" + Utility.localhost
				+ "/TaskShareWebService/restful/task/getTasks", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							if (obj.getBoolean("status")) {
								if (obj.getJSONArray("list").length() == 0) {
									Toast.makeText(getApplicationContext(),
											"Please create a task first",
											Toast.LENGTH_SHORT).show();
									finish();
								} else {
									for (int i = 0; i < obj
											.getJSONArray("list").length(); i++) {
										your_array_list.add(obj
												.getJSONArray("list")
												.getString(i).toString());
										arrayAdapter.notifyDataSetChanged();
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
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						if (statusCode == 404) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_resource_not_found),
									Toast.LENGTH_SHORT).show();
						} else if (statusCode == 500) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_server_side),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void invokeWSForTaskAssignment(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://" + Utility.localhost
				+ "/TaskShareWebService/restful/task/assigntasks", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							if (obj.getBoolean("status")) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.task_assigned),
										Toast.LENGTH_SHORT).show();
								
								
								totalDelta = Double.parseDouble(selectedTaskPoints) * (-0.2);
								for(int i =0 ;i<your_array_list.size();i++){
									String tempName = your_array_list.get(i).toString();
									String tempPoint =tempName.substring(tempName.indexOf("----")+4, tempName.length()-7);
									totalTaskPoints = totalTaskPoints +Double.parseDouble(tempPoint);
								}
								
								params2.add("groupname", groupName);
								params2.add("selecteTaskName", selectedTask);
								params2.add("totalDelta",totalDelta.toString());
								params2.add("totalTaskPoints",totalTaskPoints.toString());
								params2.add("selectedTaskPoints",selectedTaskPoints);
								invokeWSForUpdateTaskCompletion(params2);
							} else {
								Toast.makeText(getApplicationContext(),
										obj.getString("error_msg"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						if (statusCode == 404) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_resource_not_found),
									Toast.LENGTH_SHORT).show();
						} else if (statusCode == 500) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_server_side),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_occured),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

}
