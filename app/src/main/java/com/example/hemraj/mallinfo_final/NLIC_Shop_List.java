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

import com.example.hemraj.mallinfo_final.sorting.ProductList;
import com.example.hemraj.mallinfo_final.util.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Hemraj on 10/24/2016.
 */

public class NLIC_Shop_List extends AppCompatActivity {

    private String TAG = NLIC_Shop_List.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get Shops JSON
    private static String url = "https://api.myjson.com/bins/2tyf8";

    ArrayList<HashMap<String, String>> ShopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_lists);

        //Back Navigation will be Displayed
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ShopList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        registerForContextMenu(lv);

        new GetShop_NLIC().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Intent newActivity = new Intent(NLIC_Shop_List.this, ProductList.class);
                    startActivity(newActivity);
                }
            }
        });
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetShop_NLIC extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(NLIC_Shop_List.this);
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
                    JSONArray shops = jsonObj.getJSONArray("Shops_NLIC");

                    // looping through All shops
                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject c = shops.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String ContactPerson = c.getString("ContactPerson");
                        String PhoneNumber = c.getString("PhoneNumber");
                        String description = c.getString("description");
                        String ShopNumber = c.getString("ShopNumber");


                        // tmp hash map for single shop
                        HashMap<String, String> Shop = new HashMap<>();

                        // adding each child node to HashMap key => value
                        Shop.put("id", id);
                        Shop.put("name", name);
                        Shop.put("ContactPerson","Contact Person: "+ContactPerson);
                        Shop.put("PhoneNumber", "Phone Number: "+PhoneNumber);
                        Shop.put("description", description);
                        Shop.put("ShopNumber", "Shop Number: "+ShopNumber);

                        // adding shop to shop list
                        ShopList.add(Shop);
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
                                "Couldn't get json from server. Please Check your Internet Connection!",
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
                    NLIC_Shop_List.this, ShopList,
                    R.layout.shop_list, new String[]{"name", "ContactPerson", "PhoneNumber", "description", "ShopNumber"},
                    new int[]{R.id.name, R.id.contact_person, R.id.phone_number,
                            R.id.description, R.id.shop_number});

            lv.setAdapter(adapter);
        }

    }

    //Function to be called while pressing back navigation button
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, ListViewActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}

