package com.example.hemraj.mallinfo_final.sorting;

/**
 * Created by Hemraj on 12/26/2016.
 */

public class Product {

    public String modelname, size, brand, price, type;

    public Product(String modelname, String size, String brand, String price, String type) {

        this.modelname = modelname;
        this.size = size;
        this.brand = brand;
        this.price = price;
        this.type = type;
    }

    public String getModelname() {
        return modelname;
    }

    public String getSize() {
        return size;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

}
