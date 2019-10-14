package soa.work.scheduler.models;

import java.util.ArrayList;
import java.util.List;

import soa.work.scheduler.R;

import static soa.work.scheduler.data.Constants.AC_REPAIRING;
import static soa.work.scheduler.data.Constants.BIKE_MECHANIC;
import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.CAR_MECHANIC;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.GEYSER_REPAIRING;
import static soa.work.scheduler.data.Constants.LAPTOP_OR_PC_REPAIRING;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.MICROWAVE_REPAIRING;
import static soa.work.scheduler.data.Constants.MOBILE_REPAIRING;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PLUMBER;
import static soa.work.scheduler.data.Constants.REFRIGERATOR_REPAIRING;
import static soa.work.scheduler.data.Constants.WASHING_MACHINE_REPAIRING;

public class Category {
    private String categoryTitle;
    private int categoryImage;
    private double price;

    public Category(String categoryTitle, int categoryImage, double price) {
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
        this.price = price;
    }

    public Category(String categoryTitle, int categoryImage) {
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static List<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(CARPENTER, R.drawable.ic_carpenter, 299.00));
        categories.add(new Category(MECHANIC, R.drawable.ic_mechanic, 299.00));
        categories.add(new Category(AC_REPAIRING, R.drawable.ic_mechanic, 499.00));
        categories.add(new Category(BIKE_MECHANIC, R.drawable.ic_mechanic, 449.00));
        categories.add(new Category(CAR_MECHANIC, R.drawable.ic_mechanic, 599.00));
        categories.add(new Category(ELECTRICIAN, R.drawable.ic_electrician, 399.00));
        categories.add(new Category(GEYSER_REPAIRING, R.drawable.ic_electrician, 299.00));
        categories.add(new Category(LAPTOP_OR_PC_REPAIRING, R.drawable.ic_electrician, 499.00));
        categories.add(new Category(MICROWAVE_REPAIRING, R.drawable.ic_electrician, 399.00));
        categories.add(new Category(MOBILE_REPAIRING, R.drawable.ic_electrician, 399.00));
        categories.add(new Category(PLUMBER, R.drawable.ic_plumber, 499.00));
        categories.add(new Category(REFRIGERATOR_REPAIRING, R.drawable.ic_electrician, 379.00));
        categories.add(new Category(WASHING_MACHINE_REPAIRING, R.drawable.ic_electrician, 379.00));
        categories.add(new Category(PAINTER, R.drawable.ic_painter, 199.00));
        return categories;
    }
}
