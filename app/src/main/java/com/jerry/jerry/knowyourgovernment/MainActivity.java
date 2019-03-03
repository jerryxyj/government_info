package com.jerry.jerry.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private List<Official> officialList=new ArrayList<>();
    private RecyclerView recyclerView;
    private  RecyclerViewAdapter mAdapter;
    private TextView loc;
    private Location location;
    private String setAdd;
    private int selectedPostion;
    private boolean network=true;
    private boolean locationpermission=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new RecyclerViewAdapter(officialList, this);
        loc = (TextView) findViewById(R.id.address);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mlayoutManger = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mlayoutManger);

        if(!networkCheck()){
            network=false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
            builder.setMessage("Data Cannot be accessed/loaded Without an internet connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            location = new Location(this);
        }
    }

    public void onClick(View v){
        Toast.makeText(this, "You want to look official detail", Toast.LENGTH_SHORT).show();
        selectedPostion=recyclerView.getChildLayoutPosition(v);
        Official o=officialList.get(selectedPostion);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra(Official.class.getName(),o);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.location:
                if(networkCheck()) {
                    Toast.makeText(this, "You want to Edit address", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);
//                builder.setIcon(R.drawable.icon1);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setAdd = et.getText().toString();
                            officialList.clear();
                            Log.d(TAG, "let's set position" + setAdd);
                            setLocation();
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, "You want to set address: " + et.getText().toString());
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.setMessage("Enter a City,State or a Zip Code:");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
                        builder.setMessage("Data Cannot be accessed/loaded Without an internet connection");
                        builder.setTitle("No Network Connection");
                        AlertDialog dialog = builder.create();
                        dialog.show();

                }
                return true;
            case R.id.about:
                Toast.makeText(this, "show information", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivity.this, About.class);
                intent2.putExtra(Intent.EXTRA_TEXT, MainActivity.class.getSimpleName());
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocation(){
        new MyAsyncTask(this).execute();
    }

    public void setData(double lat, double lon) {
        Log.d(TAG, "setData: Lat: " + lat + ", Lon: " + lon);
        if(networkCheck()) {
            String address = doAddress(lat, lon);
            Log.d(TAG, "Address is: " + address);
            ((TextView) findViewById(R.id.address)).setText(address);
        }
        setAdd=((TextView) findViewById(R.id.address)).getText().toString();
        new MyAsyncTask(this).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        location.setUpLocationManager();
                        location.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        locationpermission=false;
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");

                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }


    private String doAddress(double latitude, double longitude) {
        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);
        List<Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                for (Address ad : addresses) {
                    Log.d(TAG, "doLocation: " + ad);
                    for (int i = 1; i < ad.getMaxAddressLineIndex(); i++)
                        sb.append("\t" + ad.getAddressLine(i) + "\n");
                }

                return sb.toString();
            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());

            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if(network&locationpermission){
            location.shutdown();
        }

        super.onDestroy();
    }



    public void updateData(ArrayList<Official> oList) {
        officialList.addAll(oList);
        Log.d(TAG,"set location value:"+oList.get(0).getLocation());
        loc.setText(oList.get(0).getLocation());
        mAdapter.notifyDataSetChanged();
    }

    public void DataError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
        builder.setMessage("You input address is not right");
        builder.setTitle("Address is not exist");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Boolean networkCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean network;
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            network=true;

        } else {
            network=false;
        }
        return network;
    }


    class MyAsyncTask extends AsyncTask<String,Integer,String> {
        private MainActivity mainActivity;
        private final String CivicInformationURL ="https://www.googleapis.com/civicinfo/v2/representatives?key=";
        public MyAsyncTask(MainActivity ma) {
            mainActivity = ma;
        }

        protected void onPreExecute(){

            Toast.makeText(mainActivity, "Loading Official Data...", Toast.LENGTH_SHORT).show();
            Log.d("Start!!!!!","1");


        }
        protected void onPostExecute(String s){
            Log.d(TAG,"Starting postExecuting: "+s);
            try {
                ArrayList<Official> officialsList = parseJSON(s);
                Log.d(TAG, "Processing postExecuting: " + officialsList.toString());
                mainActivity.updateData(officialsList);
            }catch (Exception e){
                mainActivity.DataError();
            }

        }
        protected String doInBackground(String... params){
            String APIkey="AIzaSyD0qzYxm0cun8Hmctv-Dhd_U1QtePkFnJ4";
            String urlToUse=CivicInformationURL+APIkey+"&address="+setAdd;
            Log.d(TAG,"url is: "+urlToUse);
            StringBuilder sb = new StringBuilder();
            String ss;
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');

                }
                ss=sb.toString();

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
            return ss;
        }
        private ArrayList<Official> parseJSON(String s) {
            ArrayList<Official> officialsList = new ArrayList<>();
            try {
                JSONObject jObj = new JSONObject(s);
                JSONObject jnormalizedInputObj = jObj.getJSONObject("normalizedInput");
                String location="";
                String signcity="";

                try{
                    signcity=jnormalizedInputObj.getString("city");

                }catch (Exception e){

                }
                String signstate="";
                try{
                    signstate=jnormalizedInputObj.getString("state");

                }catch (Exception e){

                }
                String signzip="";
                try{
                    signzip=jnormalizedInputObj.getString("zip");

                }catch (Exception e){

                }
                location=signcity+","+signstate+" "+signzip;
                JSONArray joffices = jObj.getJSONArray("offices");
                JSONArray jofficials=jObj.getJSONArray("officials");

                for (int i = 0; i < joffices.length(); i++) {

                    JSONObject jName = (JSONObject) joffices.get(i);
                    String title = jName.getString("name");


                    JSONArray jofficialIndices=jName.getJSONArray("officialIndices");

                    for (int j = 0; j < jofficialIndices.length(); j++) {
                        int index=(Integer)jofficialIndices.get(j);
                        JSONObject officials=(JSONObject) jofficials.get(index);
                        String name="";
                        try {
                            name = officials.getString("name");
                        }catch (Exception e){

                        }
                        String address="No data provided";
                        StringBuilder sb = new StringBuilder();
                        try {

                            JSONArray addressinfo = officials.getJSONArray("address");
                            JSONObject add = (JSONObject) addressinfo.get(0);

                            String line1="";
                            try {
                                line1 = add.getString("line1");
                                sb.append(line1).append('\n');
                            }catch (Exception e){

                            }
                            String line2="";
                            try {
                                line2 = add.getString("line2");
                                sb.append(line2).append('\n');
                            }catch (Exception e){

                            }
                            String city="";
                            try {
                                city = add.getString("city");
                                sb.append(city).append(", ");
                            }catch (Exception e){

                            }
                            String state="";
                            try {
                                state = add.getString("state");
                                sb.append(state).append(" ");
                            }catch (Exception e){

                            }
                            String zip="";
                            try {
                                zip = add.getString("zip");
                                sb.append(zip);
                            }catch (Exception e){

                            }

                            address = sb.toString();
                        }catch (Exception e){

                        }

                        String party="No data provided";
                        try {
                            party = officials.getString("party");
                        }catch (Exception e){

                        }
                        String phones="No data provided";
                        try {
                            JSONArray number = officials.getJSONArray("phones");
                            phones = (String) number.get(0);
                        }catch (Exception e){

                        }
                        String website="No data provided";
                        try {
                            JSONArray urls = officials.getJSONArray("urls");

                            website = (String) urls.get(0);
                        }catch (Exception e){

                        }

                        String email="No data provided";
                        try {
                            JSONArray emails = officials.getJSONArray("emails");
                            email = (String) emails.get(0);
                        }catch (Exception e){


                        }
                        String photoUrl="No data provided";
                        try {

                            photoUrl = officials.getString("photoUrl");
                        }catch (Exception e){

                        }
                        String googleplus="No data provided";
                        String facebook="No data provided";
                        String twitter="No data provided";
                        String youtube="No data provided";
                        try{
                        JSONArray medialchannel=officials.getJSONArray("channels");
                        for (int k = 0; k < medialchannel.length(); k++) {
                            JSONObject medial=(JSONObject)medialchannel.get(k);
                            if(medial.getString("type").equals("GooglePlus")){
                               googleplus=medial.getString("id");
                            }
                            else if(medial.getString("type").equals("Facebook")) {
                                facebook = medial.getString("id");
                            }
                            else  if(medial.getString("type").equals("Twitter")) {
                                twitter = medial.getString("id");
                            }
                            else  if(medial.getString("type").equals("YouTube")) {
                                 youtube = medial.getString("id");
                            }

                        }}catch (Exception e){

                        }
                        officialsList.add(new Official(title,name,address,party,phones,website,email,photoUrl,googleplus,facebook,twitter,youtube,location));



                    }
                }
                Log.d(TAG, "Dealing with Json: ");
                return  officialsList;

            } catch (Exception e) {
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
            return null;

        }


    }



}
