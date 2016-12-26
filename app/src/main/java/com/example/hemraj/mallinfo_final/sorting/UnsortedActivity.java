package com.example.hemraj.mallinfo_final.sorting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.hemraj.mallinfo_final.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hemraj on 12/26/2016.
 */

public class UnsortedActivity extends AppCompatActivity {


    ListView unsortedlist;
    ProductAdapter productAdapter;
    ArrayList<Product> productList = new ArrayList<Product>();
    ArrayList<String> jsonString = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsorted);

        unsortedlist = (ListView) findViewById(R.id.unsortedlist);


        SharedPreferences sp = getSharedPreferences("SHARED_PREF_NAME", Context.MODE_PRIVATE);
        String jsonString = sp.getString("jsonString", null);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {


            try {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String modelname = "Modelname:" + jsonObject.getString("modelname");
                String size = "Size:" + jsonObject.getString("size");
                String brand = "Brand:" + jsonObject.getString("brand");
                String price = "Price:" + jsonObject.getString("price");
                String type = "Type:" + jsonObject.getString("type");
                Product product = new Product(modelname, size, brand, price, type);
                productList.add(product);

            } catch (Exception e) {
            }
        }
        productAdapter = new ProductAdapter(UnsortedActivity.this, R.layout.list_layout, productList);

        unsortedlist.setAdapter(productAdapter);

        productAdapter.notifyDataSetChanged();
    }
}
