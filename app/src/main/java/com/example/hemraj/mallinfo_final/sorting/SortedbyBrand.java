package com.example.hemraj.mallinfo_final.sorting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.hemraj.mallinfo_final.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hemraj on 12/26/2016.
 */

public class SortedbyBrand extends AppCompatActivity {

    ListView unsortedlist;
    ProductAdapter productAdapter;
    String mn;
    ArrayList<Product> productList = new ArrayList<Product>();
    String jsonString;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sortedbybrand);

        //Back Navigation will be Displayed
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unsortedlist = (ListView) findViewById(R.id.unsortedlist);

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF_NAME", Context.MODE_PRIVATE);
        jsonString = sharedPreferences.getString("jsonString", null);

        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonValues.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "brand";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                } catch (JSONException e) {
                    //do something
                }

                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }


        for (int i = 0; i < sortedJsonArray.length(); i++) {

            try {
                JSONObject jsonObject = sortedJsonArray.getJSONObject(i);
                String modelname = "Model Name:   " + jsonObject.getString("modelname");
                String size = "Size:   " + jsonObject.getString("size");
                String brand = "Brand:  " + jsonObject.getString("brand");
                String price = "Price:  " + jsonObject.getString("price");
                String type = "Type:  " + jsonObject.getString("type");
                Product product = new Product(modelname, size, brand, price, type);
                productList.add(product);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            productAdapter = new ProductAdapter(SortedbyBrand.this, R.layout.list_layout, productList);

            unsortedlist.setAdapter(productAdapter);

            productAdapter.notifyDataSetChanged();
        }

    }

    // Initiating Menu XML file (menu_sort.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    //Function to be called while pressing back navigation button
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, ProductList.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;

            case R.id.sort_brand:
                Intent brandIntent = new Intent(this, SortedbyBrand.class);
                brandIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(brandIntent);
                return true;

            case R.id.sort_price:
                Intent priceIntent = new Intent(this, SortedbyPrice.class);
                priceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(priceIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}

