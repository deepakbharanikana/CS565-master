package com.iubcsse.sharetask;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends ActionBarActivity {
	Button btn_signup;
	EditText et_email, et_firstName,et_lastName, et_Password, et_cnfrmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		et_email = (EditText) findViewById(R.id.textField_email);
		et_firstName = (EditText) findViewById(R.id.textField_firstName);
		et_lastName = (EditText) findViewById(R.id.textField_lastName);
		et_Password = (EditText) findViewById(R.id.textField_Password);
		et_cnfrmPassword = (EditText) findViewById(R.id.textField_confirmPassword);
		
		btn_signup = (Button) findViewById(R.id.button_signUp);
		
		btn_signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        String fname = et_firstName.getText().toString();
		        String lname = et_lastName.getText().toString();
		        String email = et_email.getText().toString();
		        String password = et_Password.getText().toString();
		        String cnfrmPassword = et_cnfrmPassword.getText().toString();
		        RequestParams params = new RequestParams();
		        if(Utility.isNotNull(fname) && Utility.isNotNull(lname) && Utility.isNotNull(email) && Utility.isNotNull(password)&& Utility.isNotNull(cnfrmPassword)){
		            if(Utility.validate(email)){
		                params.put("name", fname);
		                params.put("username", email);
		                params.put("password", password);
		                invokeWSForSignUp(params);
		            }
		            else{
		                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
		            }
		        } 
		        else{
		            Toast.makeText(getApplicationContext(), "Please fill the form completely", Toast.LENGTH_SHORT).show();
		        }
		 
			}
		});
	}
	
	public void invokeWSForSignUp(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/register/doRegister",params ,new AsyncHttpResponseHandler() {
             @Override
             public void onSuccess(String response) {
                 try {
                         JSONObject obj = new JSONObject(response);
                         if(obj.getBoolean("status")){
                             Toast.makeText(getApplicationContext(), "You are successfully registered!", Toast.LENGTH_SHORT).show();
                         } 
                         else{
                             Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                         }
                 } catch (JSONException e) {
                     Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                     e.printStackTrace();
 
                 }
             }
             
             @Override
             public void onFailure(int statusCode, Throwable error,
                 String content) {
                 if(statusCode == 404){
                     Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_SHORT).show();
                 } 
                 else if(statusCode == 500){
                     Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_SHORT).show();
                 } 
                 else{
                     Toast.makeText(getApplicationContext(), "Unexpected Error occcured!", Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }
}
