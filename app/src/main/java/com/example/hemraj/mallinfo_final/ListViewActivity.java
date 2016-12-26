package com.example.hemraj.mallinfo_final;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.hemraj.mallinfo_v20.util.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hemraj on 9/25/2016.
 */

public class ListViewActivity extends AppCompatActivity {

    private String TAG = ListViewActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get Mall JSON
    private static String url = "https://api.myjson.com/bins/1226w";

    ArrayList<HashMap<String, String>> poiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Back Navigation will be Displayed
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        poiList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetPOIs().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Intent newActivity = new Intent(ListViewActivity.this, NLIC_Shop_List.class);
                    startActivity(newActivity);
                }



            }




        });


    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPOIs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(ListViewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("POIs");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String longitude = c.getString("longitude");
                        String latitude = c.getString("latitude");
                        String altitude = c.getString("altitude");
                       String description = c.getString("description");



                        // tmp hash map for single contact
                        HashMap<String, String> POI = new HashMap<>();

                        // adding each child node to HashMap key => value
                        POI.put("id", id);
                        POI.put("name", name);
                        POI.put("description", description);
                       // contact.put("mobile", mobile);

                        // adding contact to contact list
                        poiList.add(POI);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ListViewActivity.this, poiList,
                    R.layout.activity_list_view, new String[]{"name", "description"},
                    new int[]{R.id.name,
                    R.id.description});

            lv.setAdapter(adapter);
        }

    }

    //Function to be called while pressing back navigation button
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }


}
