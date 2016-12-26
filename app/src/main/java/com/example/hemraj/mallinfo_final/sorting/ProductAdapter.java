package com.example.hemraj.mallinfo_final.sorting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hemraj.mallinfo_final.R;

import java.util.ArrayList;

/**
 * Created by Hemraj on 12/26/2016.
 */

public class ProductAdapter  extends ArrayAdapter<Product> {
    Activity activity;
    int layoutResourceId;
    ArrayList<Product> data = new ArrayList<Product>();
    Product product;

    public ProductAdapter(Activity activity, int layoutResourceId, ArrayList<Product> data) {
        super(activity, layoutResourceId, data);

        this.activity = activity;
        this.layoutResourceId = layoutResourceId;
        this.data = data;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        LaptopHolder holder = null;


        if (row == null) {

            LayoutInflater inflater = LayoutInflater.from(activity);

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LaptopHolder();

            holder.modelname = (TextView) row.findViewById(R.id.textViewModelName);
            holder.size = (TextView) row.findViewById(R.id.textViewSize);
            holder.brand = (TextView) row.findViewById(R.id.textViewBrand);
            holder.price = (TextView) row.findViewById(R.id.textViewPrice);
            holder.type = (TextView) row.findViewById(R.id.textViewType);


            row.setTag(holder);

        } else {
            holder = (LaptopHolder) row.getTag();
        }

        product = data.get(position);

        holder.modelname.setText(product.getModelname());
        holder.size.setText(product.getSize());
        holder.brand.setText(product.getBrand());
        holder.price.setText(product.getPrice());
        holder.type.setText(product.getType());


        return row;
    }


    class LaptopHolder {

        TextView modelname, size, brand, price, type;


    }

}

