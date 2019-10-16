package soa.work.scheduler.models;

public class PriceOffer {
     private String work_price;
     private String work_onOffer;
     private String work_category;

     public PriceOffer(){
     }

    public String getWork_category() {
        return work_category;
    }

    public void setWork_category(String work_category) {
        this.work_category = work_category;
    }
    public String getWork_price() {
        return work_price;
    }

    public void setWork_price(String work_price) {
        this.work_price = work_price;
    }

    public String getWork_onOffer() {
        return work_onOffer;
    }

    public void setWork_onOffer(String work_onOffer) {
        this.work_onOffer = work_onOffer;
    }
}
