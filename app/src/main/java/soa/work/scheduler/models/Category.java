package soa.work.scheduler.models;

public class Category {
    private String categoryTitle;
    private int categoryImage;

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
}
