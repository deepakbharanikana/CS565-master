package com.iubcsse.sharetask;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends ActionBarActivity {

	ListView groupList;
	List<String> your_array_list = new ArrayList<String>();
	ArrayAdapter<String> arrayAdapter;
	String userEmail;
	RequestParams paramsForGroups = new RequestParams();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Bundle extras = getIntent().getExtras();
		userEmail = extras.getString("email");

		groupList = (ListView) findViewById(R.id.list_groupInfo);
		
		paramsForGroups.add("username", userEmail);
		invokeWSForGettingGroups(paramsForGroups);

		View footer = getLayoutInflater().inflate(R.layout.list_footer_item,
				null);
		TextView footerTitle = (TextView) footer
				.findViewById(R.id.list_item_footer_title);
		footerTitle.setText(R.string.text_groupsFooter);
		groupList.addFooterView(footer);

		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, your_array_list);

		groupList.setAdapter(arrayAdapter);

		groupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int count = arrayAdapter.getCount();
				if (count == arg2) {
					AddGroup();
				} else {
					String selectedGroupName = groupList.getItemAtPosition(arg2).toString();
					Intent intent = new Intent(getApplicationContext(),PersonsActivity.class);
					intent.putExtra("groupName", selectedGroupName);
					intent.putExtra("userName", userEmail);
					startActivity(intent);
				}
			}
		});

	}
	
	private void AddGroup() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

		alertDialog.setTitle(getResources().getString(R.string.alert_groupTitle));
		
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setOrientation(LinearLayout.VERTICAL);

		final EditText inputGroup = new EditText(getApplicationContext());
		inputGroup.setHint("Group Name");
		inputGroup.setTextColor(getResources().getColor(R.color.black));
		layout.addView(inputGroup);


		alertDialog.setView(layout);
		final RequestParams params2 = new RequestParams();

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.key);

		alertDialog.setPositiveButton(getResources().getString(R.string.alert_createBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// should add to group table n call service
						if(inputGroup.getText().toString().length()!=0){
							params2.add("Gname", inputGroup.getText().toString());
							params2.add("email", userEmail);
							invokeWSForGroupInsert(params2);
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
	
	public void invokeWSForGroupInsert(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/groups/insertgroups",params ,new AsyncHttpResponseHandler() {
             @Override
             public void onSuccess(String response) {
                 try {
                         JSONObject obj = new JSONObject(response);
                         if(obj.getBoolean("status")){
                             Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_added), Toast.LENGTH_SHORT).show();
                             your_array_list.clear();
                             invokeWSForGettingGroups(paramsForGroups);
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
	
	public void invokeWSForGettingGroups(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/groups/getGroups",
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
							Toast.makeText(getApplicationContext(),getResources().getString(R.string.err_occured), Toast.LENGTH_SHORT).show();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						if (statusCode == 404) {
							Toast.makeText(getApplicationContext(),
									getResources().getString(R.string.err_resource_not_found),	Toast.LENGTH_SHORT).show();
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
