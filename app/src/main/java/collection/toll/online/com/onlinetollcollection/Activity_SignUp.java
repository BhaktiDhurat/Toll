package collection.toll.online.com.onlinetollcollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import collection.toll.online.com.onlinetollcollection.config.ProjectConfig;


public class Activity_SignUp extends AppCompatActivity {

    //Edittext declaration
    EditText edtPassword,edtName,edtEmail,edtAddress,edtMobile1,edtAcountNumber,edtCVV;
    String strPassword,strName,strMobile1,strEamil,strAddress,strAcountNumber,strCVV;
    //login button Register
    Button buttonRegister;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);//set layout
        setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //name,usrname,mobile number initilization
        edtName=(EditText)findViewById(R.id.editTextName);
        edtMobile1=(EditText)findViewById(R.id.editTextMobile1);
        edtAddress=(EditText)findViewById(R.id.editTextAddress);

        edtPassword=(EditText)findViewById(R.id.editTextPassword);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtAcountNumber=(EditText)findViewById(R.id.editTextAccountNumber);
        edtCVV=(EditText)findViewById(R.id.editTextCVV);

        //Register button initilization
        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(Activity_SignUp.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //on click on button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidation();



        }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home);
        {
            finish();
        }

        return true;
    }

    void registerOnServer(){


        progressDialog.show();;
        strEamil=edtEmail.getText().toString();
    strPassword = edtPassword.getText().toString();
    strName = edtName.getText().toString();
        strMobile1 = edtMobile1.getText().toString();
        strAddress = edtAddress.getText().toString();
        strAcountNumber = edtAcountNumber.getText().toString();
        strCVV = edtCVV.getText().toString();

    SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
    String IP=sp.getString("IP","209.190.31.226:8080");
       RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
System.out.println("#####     name  "+strName+"   strPassword   "+strPassword+  "  strAddress  "+strAddress);
        String url=ProjectConfig.REGISTRATION+"?username="+strName+"&password="+strPassword+"&mobileNumber="+strMobile1+"&emailId="+strEamil+"&address="+strAddress+"&accountNumber="+strAcountNumber+"&cvv="+strCVV;

        http://localhost:8080/Toll_Plaza_Project/Adduserapi?username=divya&password=divya&emailId=divya10@gmail.com&mobileNumber=6767676767&address=pune&accountNumber=9999999&cvv=890890

       url=url.replace(" ","");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest("http://"+IP+ url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject jsonObject) {

                progressDialog.hide();



                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## response:" + jsonObject.toString());


                try {
                    if(jsonObject.getString("result").equals("success")){

                        //start new activity
                        Intent i=new Intent(getApplicationContext(),Activity_Login.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Registration Successful", Toast.LENGTH_SHORT).show();
                        finish();

                    }else
                        {
                            Toast.makeText(getApplicationContext(),"Registration Unsuccessful: "+jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this.createRequestRegisterErrorListener());


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





void checkValidation(){
    strPassword = edtPassword.getText().toString();
    strName = edtName.getText().toString();
    strMobile1 = edtMobile1.getText().toString();
    strAddress = edtAddress.getText().toString();
    strEamil=edtEmail.getText().toString();
    strAcountNumber=edtAcountNumber.getText().toString();
    strCVV=edtCVV.getText().toString();



    if ((!strName.equals("")) && strName.matches("[a-zA-Z]+") ) {
        if ((!strMobile1.equals(""))&&strMobile1.length()==10&&strMobile1.matches("^[7|8|9][0-9]+")) {
            if ((!strEamil.equals(""))&& strEamil.replace(" ","").matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {


            if (!strPassword.equals("")) {

                if (!strAddress.equals("")) {

                    if ((!strAcountNumber.equals("")) && strAcountNumber.length()>7) {
                        if ((!strCVV.equals("")) && strCVV.length()==3) {

                      registerOnServer();


                        } else {
                            edtCVV.setText("");
                            edtCVV.setHint("Please Enter Valid CVV");
                            edtCVV.requestFocus();
                        }

                    } else {
                        edtAcountNumber.setText("");
                        edtAcountNumber.setHint("Please Enter Valid Acount Number");
                        edtAcountNumber.requestFocus();
                    }


                } else {
                    edtAddress.setText("");
                    edtAddress.setHint("Please Enter Valid Address");
                    edtAddress.requestFocus();
                }


                } else {
                edtPassword.setText("");
                edtPassword.setHint("Please Enter Password");
                edtPassword.requestFocus();
            }


    } else {
                edtEmail.setText("");
                edtEmail.setHint("Please Enter Valid Email");
                edtEmail.requestFocus();
            }

        }else {
            edtMobile1.setText("");
            edtMobile1.setHint("Please Enter Mobile");
            edtMobile1.requestFocus();
        }
    } else {
        edtName.setText("");
        edtName.setHint("Please Enter Valid Name");
        edtName.requestFocus();
    }

}





}
