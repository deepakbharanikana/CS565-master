package com.iubcsse.sharetask;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{
	
	Button btnLogin, btnSignUp;
	EditText etEmail;
	EditText etPassword;
	TextView tvForgotPass;
	String email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnLogin = (Button) findViewById(R.id.button_login);
		btnSignUp = (Button) findViewById(R.id.button_signUp);
		tvForgotPass = (TextView) findViewById(R.id.label_forgotPassword);
		etEmail = (EditText) findViewById(R.id.textField_email);
		etPassword = (EditText) findViewById(R.id.textField_Password);
		
		etEmail.setText("testuser@indiana.edu");
		etPassword.setText("password");
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				RequestParams params = new RequestParams();
				if(Utility.isNotNull(email) && Utility.isNotNull(password)){
		            if(Utility.validate(email)){
		                params.put("username", email);
		                params.put("password", password);
		                invokeWSForLogin(params);
		            } 
		            else{
		                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_valid_email), Toast.LENGTH_SHORT).show();
		            }
		        } else{
		            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_empty_credentials), Toast.LENGTH_SHORT).show();
		        }
			}
		});
		
		btnSignUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
				        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				        startActivity(intent);
			}
		});
	}
	
	
	public void invokeWSForLogin(RequestParams params){
         AsyncHttpClient client = new AsyncHttpClient();
         client.get("http://"+Utility.localhost+"/TaskShareWebService/restful/login/loginAction",params ,new AsyncHttpResponseHandler() {
             @Override
             public void onSuccess(String response) {
                 try {
                         JSONObject obj = new JSONObject(response);
                         if(obj.getBoolean("status")){
                             LaunchMainActivity();
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
	
	public void LaunchMainActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", etEmail.getText().toString());
        startActivity(intent);
    }
}
