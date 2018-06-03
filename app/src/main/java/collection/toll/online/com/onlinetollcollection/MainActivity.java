package collection.toll.online.com.onlinetollcollection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import collection.toll.online.com.onlinetollcollection.java_class.Vehicle;
import collection.toll.online.com.onlinetollcollection.services.LocationUpdateService;

public class MainActivity extends AppCompatActivity {
boolean doubleBackToExitPressedOnce=false;
    Button btnToll,btnNearByLocation,lostButton;
    ArrayList<String> vehicle;
    ArrayList<Vehicle> vehicleInfo;
    String[] items;
    Menu menu;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
setTitle("Online Toll Collection");
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        vehicle=new ArrayList<>();
        vehicleInfo=new ArrayList<>();
        btnToll= (Button) findViewById(R.id.btnToll);
        btnNearByLocation= (Button) findViewById(R.id.btnNearByLocation);
        lostButton= (Button) findViewById(R.id.lost_My_Vehicle);

        Intent serv=new Intent(MainActivity.this, LocationUpdateService.class);
        startService(serv);

        btnToll.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i=new Intent(MainActivity.this, TollQRListActivity.class);
        startActivity(i);
    }
});

        btnNearByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        lostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVehicle();
            }
        });
    }

    @Override
    public void onBackPressed() {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("isLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isLogin", "0");
            editor.apply();
            Intent i = new Intent(MainActivity.this, Activity_Login.class);
            startActivity(i);
            finish();
            return true;
        }
        if (id == R.id.add_balance) {

            Intent i = new Intent(MainActivity.this, AddBalanceInWalletActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void updateMenuBalanceTitles(String bal) {
        MenuItem menuItem = menu.findItem(R.id.balance);

        menuItem.setTitle(bal);
    }




    void getBalance(){


        progressDialog.show();;

        SharedPreferences sp1=getSharedPreferences("myPref",MODE_PRIVATE);
        final String id = sp1.getString("id", null);


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);

        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.GET_BALANCE;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){

                        updateMenuBalanceTitles("Rs. "+jsonObject.getString("currentBalance")+"/-");

                    }else{
                        Toast.makeText(getApplicationContext(), "Fail to get balance", Toast.LENGTH_SHORT).show();

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
                            items=new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // {"vehicle Type":"4 Wheeler","userVehicle":"bmw","vehicle id":"2"}

                                JSONObject j = jsonArray.getJSONObject(i);
                                String vehicleType = j.getString("vehicle_Type");
                                String userVehicle = j.getString("userVehicle");
                                String vehicleId = j.getString("vehicle_id");
                                vehicleInfo.add(new Vehicle(vehicleType, userVehicle, vehicleId));
                                vehicle.add(userVehicle);
                                items[i]=userVehicle;
                            }


                            //  String[] items = {"One time : "+ jsonObject.getString("amount_for_one_way"), "Reurn : "+ jsonObject.getString("amount_for_return")};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Select Your Lost Vehicle");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        // Do something with the selection
                                       //    mDoneButton.setText(items[item]);
                                        setStatus(id,vehicleInfo.get(item).getVehicleId(),"1");

                                    }}).show();

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


    void setStatus(final String id, final String vehicleId, final String status){


        progressDialog.show();;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());



        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.VEHICLE_LOST;
        url=url.replace(" ","%20");
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {




            public void onResponse(String string) {

                progressDialog.hide();

                try {
                    JSONObject jsonObject=new JSONObject(string);
                    Log.i("##", "##" + jsonObject.toString());

                    System.out.println("## response:" + jsonObject.toString());



                    if(jsonObject.getString("result").equals("success")){

                        Toast.makeText(getApplicationContext(), "Status Update Succefully", Toast.LENGTH_SHORT).show();



                    }else{
                        Toast.makeText(getApplicationContext(), "Status Update Unsuccefully", Toast.LENGTH_SHORT).show();

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
                params.put("vehicleId",vehicleId);
                params.put("vehicleStatus",status);

                return params;
            }
        };


        requestQueue.add(jsObjRequest);
    }



}
