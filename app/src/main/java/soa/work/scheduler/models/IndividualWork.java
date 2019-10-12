package soa.work.scheduler.models;

public class IndividualWork {
    private String work_category;
    private String work_description;
    private String work_address;
    private String worker_phone_number;
    private String user_phone;
    private String created_date;
    private String assigned_to;
    private String assigned_to_id;
    private String assigned_at;
    private boolean work_completed;
    private String work_deadline;
    private String price_range_from;
    private String price_range_to;
    private boolean is_work_available;
    private String work_latitude;
    private String work_longitude;

    public IndividualWork() {
    }

    public String getWork_latitude() {
        return work_latitude;
    }

    public void setWork_latitude(String work_latitude) {
        this.work_latitude = work_latitude;
    }

    public String getWork_longitude() {
        return work_longitude;
    }

    public void setWork_longitude(String work_longitude) {
        this.work_longitude = work_longitude;
    }

    public boolean getIs_work_available() {
        return is_work_available;
    }

    public void setIs_work_available(boolean is_work_available) {
        this.is_work_available = is_work_available;
    }

    public String getPrice_range_from() {
        return price_range_from;
    }

    public void setPrice_range_from(String price_range_from) {
        this.price_range_from = price_range_from;
    }

    public String getPrice_range_to() {
        return price_range_to;
    }

    public void setPrice_range_to(String price_range_to) {
        this.price_range_to = price_range_to;
    }

    public String getWork_category() {
        return work_category;
    }

    public void setWork_category(String work_category) {
        this.work_category = work_category;
    }

    public String getWork_description() {
        return work_description;
    }

    public void setWork_description(String work_description) {
        this.work_description = work_description;
    }

    public String getWork_address() {
        return work_address;
    }

    public void setWork_address(String work_address) {
        this.work_address = work_address;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getAssigned_at() {
        return assigned_at;
    }

    public void setAssigned_at(String assigned_at) {
        this.assigned_at = assigned_at;
    }

    public boolean getWork_completed() {
        return work_completed;
    }

    public void setWork_completed(boolean work_completed) {
        this.work_completed = work_completed;
    }

    public String getWork_deadline() {
        return work_deadline;
    }

    public void setWork_deadline(String work_deadline) {
        this.work_deadline = work_deadline;
    }

    public String getAssigned_to_id() {
        return assigned_to_id;
    }

    public void setAssigned_to_id(String assigned_to_id) {
        this.assigned_to_id = assigned_to_id;
    }

    public String getWorker_phone_number() {
        return worker_phone_number;
    }

    public void setWorker_phone_number(String worker_phone_number) {
        this.worker_phone_number = worker_phone_number;
    }
}
