package user.foodclearance.team9.com.foodclearanceuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by KhizerHasan on 12/7/2015.
 */
public class SubscribedListActivity extends AppCompatActivity{

   // static final String APP_SERVER_URL = "http://project9.comxa.com/php/db_insert_users.php";
    static final String APP_SERVER_URL = "http://project9.comxa.com/php/db_select_subscribed_owners.php";
    static final String APP_SERVER_URL_unsubscribe = "http://project9.comxa.com/php/db_delete_subscribe.php";
    ProgressDialog prgDialog;
    Context applicationContext;
    JSONObject jsonParams = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_subscribed_list);

        SharedPreferences pref=getSharedPreferences("data", 0);
        final String username=pref.getString("username","");
        // Intent i = getIntent();
        // String username = i.getStringExtra("username");

        applicationContext = getApplicationContext();
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        final ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
            }
        });

        prgDialog.show();
        try
        {
            jsonParams.put("username", username);
        }

        catch (Exception e){
            Log.d("Error", e.getMessage());
        }

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            Log.d("Error", e.getMessage());
        }
        client.post(applicationContext, APP_SERVER_URL, entity, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }


                        Log.d("Response",responseBody.toString());
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }


                        try
                        {

                            int length = jsonArray.length();
                            List<String> listContents = new ArrayList<String>(length);
                            for (int i = 0; i < length; i++)
                            {
                                JSONObject j =  jsonArray.getJSONObject(i);
                                Log.d("JSON",j.toString());
                                String StoreName = j.getString("sName");
                                listContents.add(StoreName);
                            }

                            myListView.setAdapter(new ArrayAdapter<String>(SubscribedListActivity.this, android.R.layout.simple_list_item_1, listContents));
                        }
                        catch (Exception e)
                        {
                            Log.d("Exception", e.getMessage());
                            // this is just an example
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {

                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Button unsubscribe = (Button) findViewById(R.id.unsubscribe);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prgDialog.show();
                try {
                    jsonParams.put("username", username);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
                AsyncHttpClient client = new AsyncHttpClient();
                StringEntity entity = null;
                try {
                    entity = new StringEntity(jsonParams.toString());
                } catch (UnsupportedEncodingException e) {
                    Log.d("Error", e.getMessage());
                }
                client.post(applicationContext, APP_SERVER_URL_unsubscribe, entity, "application/json",
                        new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                // Hide Progress Dialog
                                prgDialog.hide();
                                if (prgDialog != null) {
                                    prgDialog.dismiss();
                                }
                                Log.d("Response", responseBody.toString());
                            }


                            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                                // Hide Progress Dialog
                                prgDialog.hide();
                                if (prgDialog != null) {
                                    prgDialog.dismiss();
                                }
                                try {

                                    int length = jsonArray.length();
                                    List<String> listContents = new ArrayList<String>(length);
                                    for (int i = 0; i < length; i++) {
                                        JSONObject j = jsonArray.getJSONObject(i);
                                        Log.d("JSON", j.toString());
                                        String CityName = j.getString("sCity");
                                        String StoreName = j.getString("sName");
                                        listContents.add(StoreName);
                                    }

                                    myListView.setAdapter(new ArrayAdapter<String>(SubscribedListActivity.this, android.R.layout.simple_list_item_1, listContents));
                                } catch (Exception e) {
                                    Log.d("Exception", e.getMessage());
                                    // this is just an example
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {

                                prgDialog.hide();
                                if (prgDialog != null) {
                                    prgDialog.dismiss();
                                }
                                // When Http response code is '404'
                                if (statusCode == 404) {
                                    Toast.makeText(applicationContext,
                                            "Requested resource not found",
                                            Toast.LENGTH_LONG).show();
                                }
                                // When Http response code is '500'
                                else if (statusCode == 500) {
                                    Toast.makeText(applicationContext,
                                            "Something went wrong at server end",
                                            Toast.LENGTH_LONG).show();
                                }
                                // When Http response code other than 404, 500
                                else {
                                    Toast.makeText(
                                            applicationContext,
                                            "Unexpected Error occcured! [Most common Error: Device might "
                                                    + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(id==R.id.action_add){
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

   /* //COde for GCM
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcmObj;
    String regId = "";
    private static final String GOOGLE_PROJ_ID = "647304135477";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_list);

        Intent i = getIntent();
        username = i.getStringExtra("username");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSubscribedList);
        setSupportActionBar(toolbar);

        applicationContext = getApplicationContext();



        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        Button b = (Button) findViewById(R.id.subscribeButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();

            }
        });

    }


    public void RegisterUser() {
        if (checkPlayServices()) {
            registerInBackground();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                        finish();
            }
            return false;
        } else {
            Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }



    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;
                    Log.d("R ID", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {

                    Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();

                    storeRegIdinServer();
                } else {
                    Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);


    }

    private void storeRegIdinServer() {
        prgDialog.show();
        try {
            jsonParams.put("regId", regId);
            jsonParams.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            Log.d("Error", e.getMessage());
        }

        client.post(applicationContext , GCM_REGID_URL, entity, "application/json",
                new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(applicationContext,
                                "Reg Id shared successfully with Web App ",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if(id==R.id.action_subscribe){
            Intent intent = new Intent(this, AddSubscriptionActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
*/
}
