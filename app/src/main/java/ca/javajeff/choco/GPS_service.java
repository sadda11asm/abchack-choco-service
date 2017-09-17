package ca.javajeff.choco;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.net.Proxy.Type.HTTP;


/**
 * Created by Саддам on 14.09.2017.
 */

public class GPS_service extends Service {

    public static ArrayList<Double> lonCoordinates = new ArrayList<>();
    public static ArrayList<Double> latCoordinates = new ArrayList<>();
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 0;
    public static final String APP_PREFERENCES = "mysettings";
    public static final  String APP_PREFERENCES_LON="Longitude";
    public static final  String APP_PREFERENCES_LAT="Latitude";
    public static ArrayList<Double> favourite = new ArrayList<Double>();
    public static JSONObject jsonObject = new JSONObject();
    public static JSONObject responseObject = new JSONObject();
    ArrayList<Integer> in = new ArrayList<Integer>();

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;


        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            fn_update(location);
            latCoordinates.add(location.getLatitude());
            lonCoordinates.add(location.getLongitude());
            in.add(0);
            MapsActivity.latitudeCoordinates.add(location.getLatitude());
            MapsActivity.longitudeCoordinates.add(location.getLongitude());
            Log.v("lat coordinates", latCoordinates.toString());
            Log.v("lon coordinates", lonCoordinates.toString());
            try {
                selectFavourites(latCoordinates,lonCoordinates);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.v("distance", String.valueOf(dist));
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private void selectFavourites(ArrayList<Double> a, ArrayList<Double> b) throws JSONException, IOException {
        for (int i=0; i<a.size();i++) {
            Log.v("iii", String.valueOf(i));
            for (int j=0;j<a.size();j++) {
                if (j!=i) {
                    if (distance(a.get(i), b.get(i),a.get(j), b.get(j))<=0.5) {
                        int inCircle = in.get(i);
                        inCircle++;
                        ArrayList<Integer> in2 = new ArrayList<Integer>();
                        for(int k=0;k<in.size();k++) {
                            if (k!=j) {
                                in2.add(in.get(k));
                            } else {
                                in2.add(inCircle);
                            }
                        }
                        in=new ArrayList<Integer>(in2);
                    }
                }
            }
            if(in.get(i) >=5) {
                favourite.clear();
                favourite.add(a.get(i));
                favourite.add(b.get(i));
                toJson(favourite);
                Log.v("json", String.valueOf(jsonObject));
                sendRequest();
                Log.v("favourite", String.valueOf(favourite));
            }
        }
    }

    private void sendRequest() throws IOException, JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", "1223");
        params.put("lat", jsonObject.getString("lat"));
        params.put("lon", jsonObject.getString("lon"));

        JsonObjectRequest req = new JsonObjectRequest("http://192.168.43.31:8000/api/get/", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.v("response", String.valueOf(response));
                            Intent intent = new Intent(GPS_service.this, MainActivity.class);
                            responseObject=response;
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

// add the request object to the queue to be executed
        Log.v("reqq", String.valueOf(req));
        ApplicationController.getInstance().addToRequestQueue(req);
    }


//    class AsyncT extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            try {
//                URL url = new URL("http://192.168.43.110:8000/api/"); //Enter URL here
//                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
//                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
//                httpURLConnection.connect();
//
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("para_1", "arg_1");
//
//                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes(jsonObject.toString());
//                wr.flush();
//                wr.close();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//
//    }

//    private String getServerResponse(JSONObject json) throws UnsupportedEncodingException {
////        HttpPost post = new HttpPost("http://192.168.43.110:8000/api");
////        StringEntity entity = new StringEntity(json.toString());
////        post.setEntity(entity);
////        post.setHeader("Content-type", "application/json");
////        DefaultHttpClient client = new DefaultHttpClient();
////        BasicResponseHandler handler = new BasicResponseHandler();
////        try {
////            String response = client.execute(post, handler);
////            return response;
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        return "de1";
//
//    }

    private void toJson(ArrayList<Double> favourite) throws JSONException {
        jsonObject.put("token", "1223");
        jsonObject.put("lat", favourite.get(0));
        jsonObject.put("lon", favourite.get(1));
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
//        try {
//            jsonObject.put("token", "145");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            jsonObject.put("lat", "50.5");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            jsonObject.put("lon", "71.0");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.i("jsonn", jsonObject.toString());
//        try {
//            sendRequest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (mySharedPreferences.contains(APP_PREFERENCES_LAT)) {
//            latCoordinates = (ArrayList<String>) mySharedPreferences.getStringSet(APP_PREFERENCES_LAT, null);
//            lonCoordinates = (ArrayList<String>) mySharedPreferences.getStringSet(APP_PREFERENCES_LON, null);
//        }
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void fn_update(Location location) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("lon", location.getLongitude());
        intent.setAction("NOW");
        sendBroadcast(intent);
    }
}
