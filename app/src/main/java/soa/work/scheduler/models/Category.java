package soa.work.scheduler.models;

import java.util.ArrayList;
import java.util.List;

import static soa.work.scheduler.data.Constants.AC_REPAIRING;
import static soa.work.scheduler.data.Constants.BIKE_MECHANIC;
import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.CAR_MECHANIC;
import static soa.work.scheduler.data.Constants.CATERING;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.HOME_TUTOR;
import static soa.work.scheduler.data.Constants.LAPTOP_OR_PC_REPAIRING;
import static soa.work.scheduler.data.Constants.MARVEL;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.MOBILE_REPAIRING;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PHOTOGRAPHY;
import static soa.work.scheduler.data.Constants.PLUMBER;
import static soa.work.scheduler.data.Constants.REFRIGERATOR_REPAIRING;
import static soa.work.scheduler.data.Constants.RENOVATION;
import static soa.work.scheduler.data.Constants.STUDENT_PROJECT;
import static soa.work.scheduler.data.Constants.T_SHIRT;
import static soa.work.scheduler.data.Constants.VOLUNTEER;
import static soa.work.scheduler.data.Constants.WASHING_MACHINE_REPAIRING;
import static soa.work.scheduler.data.Constants.WEDDING;

public class Category {
    private String categoryTitle;
    private String categoryImageFileName;
    private String price;
    private String status;
    private String work_onOffer;


    public Category(String categoryTitle, String categoryImageFileName, String status) {
        this.categoryTitle = categoryTitle;
        this.categoryImageFileName = categoryImageFileName;
        this.status = status;
    }

    public Category(String categoryTitle, String categoryImageFileName) {
        this.categoryTitle = categoryTitle;
        this.categoryImageFileName = categoryImageFileName;
    }

    public String getWork_onOffer() {
        return work_onOffer;
    }

    public void setWork_onOffer(String work_onOffer) {
        this.work_onOffer = work_onOffer;
    }
    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryImageFileName() {
        return categoryImageFileName;
    }

    public void setCategoryImageFileName(String categoryImageFileName) {
        this.categoryImageFileName = categoryImageFileName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static List<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(CARPENTER, "", ""));
        categories.add(new Category(MECHANIC,"", ""));
        categories.add(new Category(AC_REPAIRING,"ac_repair.jpg", ""));
        categories.add(new Category(BIKE_MECHANIC,"bike_mechanic.jpg", ""));
        categories.add(new Category(CAR_MECHANIC,"car_mechanic.jpg", ""));
        categories.add(new Category(ELECTRICIAN, "commercial_electrician.jpg", ""));
        categories.add(new Category(LAPTOP_OR_PC_REPAIRING, "laptop_pc_repairing.jpg", ""));
        categories.add(new Category(MOBILE_REPAIRING, "mobile_repairing.jpg", ""));
        categories.add(new Category(PLUMBER, "plumber.jpg", ""));
        categories.add(new Category(REFRIGERATOR_REPAIRING,"refrigerator_repairing.jpg", ""));
        categories.add(new Category(WASHING_MACHINE_REPAIRING, "washing_machine.jpg", ""));
        categories.add(new Category(PAINTER,"", ""));
        categories.add(new Category(MARVEL, "", ""));
        categories.add(new Category(PHOTOGRAPHY, "photography_videography.jpg", ""));
        //Softwatare solution price
        categories.add(new Category(CATERING, "catering.jpg", ""));
        categories.add(new Category(T_SHIRT, "t_shirt.jpg", ""));
        categories.add(new Category(WEDDING, "wedding.jpg", ""));
        categories.add(new Category(STUDENT_PROJECT, "project-Copy.jpg", ""));
        categories.add(new Category(VOLUNTEER, "", ""));
        categories.add(new Category(HOME_TUTOR, "home_tutor.jpg", ""));
        categories.add(new Category(RENOVATION, "renovation_service.jpg", ""));
        return categories;
    }
}
