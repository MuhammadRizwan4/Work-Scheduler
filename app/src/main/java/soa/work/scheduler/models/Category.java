package soa.work.scheduler.models;

import java.util.ArrayList;
import java.util.List;

import static soa.work.scheduler.data.Constants.CARPENTER;

public class Category {
    private String categoryTitle;
    private String categoryImage;
    private double price;

    public Category(String categoryTitle, String categoryImage, double price) {
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
        this.price = price;
    }

    public Category(String categoryTitle, String categoryImage) {
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
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
        categories.add(new Category(CARPENTER, "https://drive.google.com/open?id=18rJWIWAIi1UWtu-8kjSx2wqPyTHZKEoy", 299.00));
//        categories.add(new Category(MECHANIC, R.drawable.ic_mechanic, 299.00));
//        categories.add(new Category(AC_REPAIRING, R.drawable.ic_mechanic, 499.00));
//        categories.add(new Category(BIKE_MECHANIC, R.drawable.ic_mechanic, 449.00));
//        categories.add(new Category(CAR_MECHANIC, R.drawable.ic_mechanic, 599.00));
//        categories.add(new Category(ELECTRICIAN, R.drawable.ic_electrician, 399.00));
//        categories.add(new Category(GEYSER_REPAIRING, R.drawable.ic_electrician, 299.00));
//        categories.add(new Category(LAPTOP_OR_PC_REPAIRING, R.drawable.ic_electrician, 499.00));
//        categories.add(new Category(MICROWAVE_REPAIRING, R.drawable.ic_electrician, 399.00));
//        categories.add(new Category(MOBILE_REPAIRING, R.drawable.ic_electrician, 399.00));
//        categories.add(new Category(PLUMBER, R.drawable.ic_plumber, 499.00));
//        categories.add(new Category(REFRIGERATOR_REPAIRING, R.drawable.ic_electrician, 379.00));
//        categories.add(new Category(WASHING_MACHINE_REPAIRING, R.drawable.ic_electrician, 379.00));
//        categories.add(new Category(PAINTER, R.drawable.ic_painter, 199.00));
//        categories.add(new Category(MAKEUP, R.drawable.ic_plumber, 499.00));
        return categories;
    }
}
