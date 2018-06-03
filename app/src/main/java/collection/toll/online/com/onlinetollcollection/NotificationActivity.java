package collection.toll.online.com.onlinetollcollection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import collection.toll.online.com.onlinetollcollection.config.ProjectConfig;
import collection.toll.online.com.onlinetollcollection.database.SQLiteAdapter;
import collection.toll.online.com.onlinetollcollection.java_class.Vehicle;

public class NotificationActivity extends AppCompatActivity {
ListView listView;
    ArrayList<String> vehicle;
    ArrayList<Vehicle> vehicleInfo;
    String tollName,tollId;
    ProgressDialog progressDialog;
    ArrayAdapter<String> a;
    String amount,status;
    SQLiteAdapter dbHelper;
    String number;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        dbHelper=new SQLiteAdapter(getApplicationContext());
        listView= (ListView) findViewById(R.id.list);
        vehicle=new ArrayList<>();
        vehicleInfo=new ArrayList<>();
        Intent intent=getIntent();
        tollName=intent.getStringExtra("tollName");
        tollId=intent.getStringExtra("tollId");
        Log.i("####"," tollId : "+tollId);

        a=new ArrayAdapter<String>(NotificationActivity.this,android.R.layout.simple_list_item_1,vehicle);
        listView.setAdapter(a);
        getVehicle();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("####"," i : "+i);


                getVehicleFare(vehicleInfo.get(i).getVehicleType());


            }
        });

    }


    void getVehicle(){


        progressDialog.show();;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id=sp1.getString("id",null);
        Log.d("######## ","  id  :  "+id);


        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.USER_VEHICLE;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){

                        JSONArray jsonArray=jsonObject.getJSONArray("vehicle");
                        if(jsonArray.length()!=0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // {"vehicle Type":"4 Wheeler","userVehicle":"bmw","vehicle id":"2"}

                                JSONObject j = jsonArray.getJSONObject(i);
                                String vehicleType = j.getString("vehicle_Type");
                                String userVehicle = j.getString("userVehicle");
                                String vehicleId = j.getString("vehicle_id");
                                vehicleInfo.add(new Vehicle(vehicleType, userVehicle, vehicleId));
                                vehicle.add(userVehicle);
                                a.notifyDataSetChanged();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "User dont have vehicle", Toast.LENGTH_SHORT).show();

                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Fail to get Vehicles", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },  new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                progressDialog.hide();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("userid",id);

                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }



    void getVehicleFare(final String vehicleType){


        progressDialog.show();;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id=sp1.getString("id",null);
        Log.d("######## ","  id  :  "+id);


        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.USER_VEHICLE_FARE;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    final JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){


                         String[] items = {"One time : "+ jsonObject.getString("amount_for_one_way"), "Reurn : "+ jsonObject.getString("amount_for_return")};

                        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                        builder.setTitle("Make your selection");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                             //   mDoneButton.setText(items[item]);
                                amount="0";
                                status="0";
                                if(item==0){
                                    try {
                                        amount= jsonObject.getString("amount_for_one_way");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                                        amount= jsonObject.getString("amount_for_return");
                                        status="1";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                JSONObject j=new JSONObject();
                                try {
                                    j.put("id",id);
                                    j.put("status",status);
                                    j.put("amount",amount);
                                    j.put("tollId",tollId);
                                    j.put("tollName",tollName);

                                    dbHelper.openToWrite();
                                    dbHelper.insertToll(tollName,tollId,status,amount,id);
                                    dbHelper.close();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent i=new Intent(NotificationActivity.this,QRcodeActivity.class);
                                i.putExtra("j",j.toString());
                                startActivity(i);
                                finish();

                               /* new AlertDialog.Builder(NotificationActivity.this)
                                        .setTitle("Payment Proceed")
                                        .setMessage("Are you sure you want to proceed?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                payFare(amount);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();*/

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();




                    }else{
                        Toast.makeText(getApplicationContext(), "Fail to get Fair", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },  new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                progressDialog.hide();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("vehicletype",vehicleType);
                params.put("tollId",tollId);

                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }


    void payFare(final String fare){


        progressDialog.show();;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id=sp1.getString("id",null);
        Log.d("######## ","  id  :  "+id);


        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.FARE_PAYMENT;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    final JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){






                    }else{
                        Toast.makeText(getApplicationContext(), "Fail to get Fair", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },  new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                progressDialog.hide();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("payamount",fare);
                params.put("userid",id);

                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }


    public void other(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotificationActivity.this);
        alertDialog.setTitle("Vehicle Number");
        alertDialog.setMessage("Enter vehicle number");

          input = new EditText(NotificationActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        number = input.getText().toString();
                        if (!number.equals("")) {
                                number=number.replace(" ","");
                            getVehicleFareOther( number);



                        }else{
                            Toast.makeText(NotificationActivity.this,"Please Enter Vehicle Number",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


}








    void getVehicleFareOther(final String vehicleNumber){


        progressDialog.show();;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id=sp1.getString("id",null);
        Log.d("######## ","  id  :  "+id);


        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.OTHER_VEHICLE_FARE;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    final JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){


                        String[] items = {"One time : "+ jsonObject.getString("amount_for_one_way"), "Reurn : "+ jsonObject.getString("amount_for_return")};

                        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                        builder.setTitle("Make your selection");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                //   mDoneButton.setText(items[item]);
                                amount="0";
                                status="0";
                                if(item==0){
                                    try {
                                        amount= jsonObject.getString("amount_for_one_way");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                                        amount= jsonObject.getString("amount_for_return");
                                        status="1";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                JSONObject j=new JSONObject();
                                try {
                                    j.put("id",id);
                                    j.put("status",status);
                                    j.put("amount",amount);
                                    j.put("tollId",tollId);
                                    j.put("tollName",tollName);

                                    dbHelper.openToWrite();
                                    dbHelper.insertToll(tollName,tollId,status,amount,id);
                                    dbHelper.close();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent i=new Intent(NotificationActivity.this,QRcodeActivity.class);
                                i.putExtra("j",j.toString());
                                startActivity(i);
                                finish();

                               /* new AlertDialog.Builder(NotificationActivity.this)
                                        .setTitle("Payment Proceed")
                                        .setMessage("Are you sure you want to proceed?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                payFare(amount);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();*/

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();




                    }else{
                        Toast.makeText(getApplicationContext(), "Fail to get Fair", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },  new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                progressDialog.hide();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("vehicleNumber",vehicleNumber);
                params.put("tollId",tollId);

                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }



}
