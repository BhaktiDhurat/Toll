package collection.toll.online.com.onlinetollcollection;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import collection.toll.online.com.onlinetollcollection.config.ProjectConfig;
import collection.toll.online.com.onlinetollcollection.services.MyFirebaseMessagingService;

public class Activity_Login extends ActionBarActivity {
    Dialog d;

    //Edittext declaration
    EditText edtUserName,edtPassword;
    String strUserName,strPassword;

    //login button declaration
    Button btnLogin,buttonSignUp;
  String fcmId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        setTitle("LOGIN");
       //username initilization
        edtUserName=(EditText)findViewById(R.id.editTextUserName);
        //Password initilization
        edtPassword=(EditText)findViewById(R.id.editTextPassword);

        // //Login button initilization
        btnLogin=(Button)findViewById(R.id.buttonLogin);
        buttonSignUp=(Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(Activity_Login.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        //on click on button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get username and password from edittext and save in variable
                strUserName=edtUserName.getText().toString();
                strPassword=edtPassword.getText().toString();

                if(!strUserName.equals(""))
                {

                    if(!strPassword.equals(""))
                    {

                       checkLogin();

                    }else{
                        edtPassword.setText("");
                        edtPassword.setHint("Please Enter Password");
                        edtPassword.requestFocus();
                    }

                }else{
                    edtUserName.setText("");
                    edtUserName.setHint("Please Enter Email");
                    edtUserName.requestFocus();
                }


            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start signup activity
                Intent i=new Intent(getApplicationContext(),Activity_SignUp.class);
                startActivity(i);
            }
        });
    }




    void checkLogin(){


        progressDialog.show();;


        SharedPreferences sp1=getSharedPreferences("fcmID",MODE_PRIVATE);
        fcmId=sp1.getString("fcmId","");
        Log.e("#####", "fcmId :" + fcmId);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);

        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.LOGIN;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            public void onResponse(String string) {

                progressDialog.hide();

                try {
                JSONObject jsonObject=new JSONObject(string);
                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## response:" + jsonObject.toString());



                   if(jsonObject.getString("result").equals("success")){



                     //   setRecurringAlarm(getApplicationContext());
                        //show message
                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("isLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("isLogin", "1");
                        editor.commit();

SharedPreferences sp=getSharedPreferences("myPref",MODE_PRIVATE);
                       Log.d("######## ","  id  :  "+jsonObject.getInt("id"));
                       SharedPreferences.Editor e=sp.edit();
                       e.putString("id",jsonObject.getInt("id")+"");
e.apply();
                       MyFirebaseMessagingService.previousTollId="0";

                       //when login success then go to new activity
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);

                        startActivity(i);
                        finish();


                    }else{
                        Toast.makeText(getApplicationContext(), "Login Unsuccessfull", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, this.createRequestRegisterErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("emailId",strUserName);
                params.put("password",strPassword);
                params.put("fcmId",fcmId);
                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }

    /**
     * Error Listener of the requested url
     * @return Response.ErrorListener
     */
    private Response.ErrorListener createRequestRegisterErrorListener() {
        return new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                progressDialog.hide();

            }
        };
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ip, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_ip:
                d = new Dialog(Activity_Login.this);
                d.setTitle("Set IP");
                d.setContentView(R.layout.dialog);
                d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final EditText ip = (EditText) d.findViewById(R.id.ip);
                Button submit = (Button) d.findViewById(R.id.submit);
                SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
                String ipStr=sp.getString("IP","209.190.31.226:8080");
                ip.setText(ipStr);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str=ip.getText().toString();
                        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
                        SharedPreferences.Editor e=sp.edit();
                        e.putString("IP",str);
                        ///  ProjectConfig.IP=str;
                        d.dismiss();
                        e.apply();
                    }
                });
                d.show();
                //  Toast.makeText(this, "Option1", Toast.LENGTH_SHORT).show();
                return true;

        }
        return true;
    }

}
