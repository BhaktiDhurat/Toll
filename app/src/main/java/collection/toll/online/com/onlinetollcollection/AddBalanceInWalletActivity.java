package collection.toll.online.com.onlinetollcollection;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import collection.toll.online.com.onlinetollcollection.config.ProjectConfig;

public class AddBalanceInWalletActivity extends AppCompatActivity {
ProgressDialog progressDialog;
    EditText edtAmount,edtAcountNumber,edtCVV,edtDate;
    Button btnSubmit;

    private DatePickerDialog datePickerDialog;

    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance_in_wallet);

        setTitle("Add Amount");
        progressDialog = new ProgressDialog(AddBalanceInWalletActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        btnSubmit= (Button) findViewById(R.id.btnSubmit);
        edtAmount= (EditText) findViewById(R.id.amount);
        edtAcountNumber= (EditText) findViewById(R.id.acountNumber);
        edtCVV= (EditText) findViewById(R.id.cvv);
        edtDate= (EditText) findViewById(R.id.date);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);



        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=edtAmount.getText().toString();
                String strAcountNumber=edtAcountNumber.getText().toString();
                String strCVV=edtCVV.getText().toString();

                if((!amount.equals(""))&&(!amount.equals("0"))) {


                    if ((!strAcountNumber.equals("")) && strAcountNumber.length()>7) {
                        if ((!strCVV.equals("")) && strCVV.length()==3) {


                            addBalance(amount,strAcountNumber,strCVV);


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
                }else{
                    Toast.makeText(AddBalanceInWalletActivity.this,"Please Enter Valid Amount",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    void addBalance(final String amount, final String acountNumber, final String cvv){


        progressDialog.show();;

        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id = sp1.getString("id", null);


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);

        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.ADD_BALANCE;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){

                        Toast.makeText(AddBalanceInWalletActivity.this,"Balance Added Successfully",Toast.LENGTH_SHORT).show();
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "Trasaction Unsuccessful : "+jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, this.createRequestRegisterErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("userid",id);
                params.put("currentBalance",amount);
                params.put("accountNumber",acountNumber);
                params.put("cvv",cvv);

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

}
