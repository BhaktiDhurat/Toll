package collection.toll.online.com.onlinetollcollection.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import collection.toll.online.com.onlinetollcollection.config.ProjectConfig;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000; // 30 sec
    private static int FATEST_INTERVAL = 5000; // 15 sec
    private static int DISPLACEMENT = 1; // 10 meters

    public double lat, lon;

    public String id;

    private boolean isAlive;


//private ParseJson parseJson = new ParseJson();

    @Override
    public void onDestroy() {

        try {
            Toast.makeText(getApplicationContext(),"destoy service",Toast.LENGTH_LONG).show();
            stopLocationUpdates();

        } catch (Exception e) {
            Log.d("###", "exception stopping service " + e.toString());
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {





            if (checkPlayServices()) {

                Log.d("####", "checking play services");

                buildGoogleApiClient();

                createLocationRequest();

            }


            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();

                Log.e("####", "client connect called");
            }


            Toast.makeText(getApplicationContext(), "Location service started", Toast.LENGTH_SHORT).show();





        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("####", "Location service onHandle intent");




    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();


        Log.e("####", "building client for location updates");
    }


    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        int resultCode = api.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {

            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
    }


    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters

        Log.e("####", " Creating location request");

    }

    /**
     * Starting the location updates
     * */


    protected void startLocationUpdates() {

        Log.e("####", " Starting location updates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        System.out.print("#### google api client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.print("####    connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {

       Log.e("########","######    Changed location : lat = " + location.getLatitude() + " lon : " + location.getLongitude() + " " + location.getAccuracy());

     //   Toast.makeText(getApplicationContext(),"New Location found",Toast.LENGTH_SHORT).show();

        lat = location.getLatitude();
        lon = location.getLongitude();

sendLocationUpdates(lat, lon);



    }


    private void sendLocationUpdates(final double lat, final double lon) {

        Log.e("####","sending location updates ");
        SharedPreferences sp=getSharedPreferences("myPref",MODE_PRIVATE);
        id = sp.getString("id", null);

        Log.d("######## ","  id  :  "+id);
        SharedPreferences ip=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=ip.getString("IP","209.190.31.226:8080");
        String url="http://"+IP+ ProjectConfig.UPDATE_LOCATION;
        //  Toast.makeText(getApplicationContext(),"sending location updates",Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
       // Toast.makeText(getApplicationContext(),"Location Upadate call",Toast.LENGTH_SHORT).show();
        JsonObjectRequest updateRequest = new JsonObjectRequest(url+"?userid="+id+"&userlat="+lat+"&userlong="+lon,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.get("result").equals("success")){

                      //  Toast.makeText(getApplicationContext(),"Location Upadated",Toast.LENGTH_SHORT).show();

                        System.out.print("##     ### Location Upadated");
                     //   Toast.makeText(getApplicationContext(),"Location Upadated",Toast.LENGTH_SHORT).show();


                    }else{
                      //  Toast.makeText(getApplicationContext(),"Location Upadate unsuccessful",Toast.LENGTH_SHORT).show();
                    }












                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  Log.d("####","Location Update sent response "+response);

              /*  try {
                    JSONObject object = parseJson.getJSONFromString(response);

                    Log.d("####","Location Update sent response "+response);

                    if (object.getString(LinkConfig.STATUS).equals(LinkConfig.SUCCESS)){


                        Log.d("####","Location Update sent ");

                    }else {

                        Log.d("####","Location Update Not sent response ");


                    }


   } catch (JSONException e) {
                    e.printStackTrace();
                }
*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error in loacation update",Toast.LENGTH_SHORT).show();
               System.out.print("####       Location Update error " + error.toString());
            }
        });
        /*{

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String>params = new HashMap<>();

                Log.d("####","crfid:  "+id);

                params.put("crefid", "\'"+id+"\'");
                params.put("lat","\'"+String.valueOf(lat)+"\'");
                params.put("longitude","\'"+String.valueOf(lon)+"\'");

                return params;
            }
        };*/


  //   MySingleton.getInstance(getApplicationContext()).addToRequestQueue(updateRequest);

        requestQueue.add(updateRequest);
    }
}
