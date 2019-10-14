package soa.work.scheduler.models;

public class UniversalWork {
    private String assignedAt;
    private String assignedTo;
    private String assignedToId;
    private String workerPhoneNumber;
    private String createdDate;
    private String userPhone;
    private String workAddress;
    private String workCategory;
    private boolean workCompleted;
    private String workDeadline;
    private String workDescription;
    private String workPostedByAccountId;
    private String workPostedByName;
    private String priceStartsAt;
    private String latitude;
    private String longitude;

    public UniversalWork() {
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPriceStartsAt() {
        return priceStartsAt;
    }

    public void setPriceStartsAt(String priceStartsAt) {
        this.priceStartsAt = priceStartsAt;
    }

    public String getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(String assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(String workCategory) {
        this.workCategory = workCategory;
    }

    public void setWorkCompleted(boolean workCompleted) {
        this.workCompleted = workCompleted;
    }

    public String getWorkDeadline() {
        return workDeadline;
    }

    public void setWorkDeadline(String workDeadline) {
        this.workDeadline = workDeadline;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public String getWorkPostedByAccountId() {
        return workPostedByAccountId;
    }

    public void setWorkPostedByAccountId(String workPostedByAccountId) {
        this.workPostedByAccountId = workPostedByAccountId;
    }

    public String getWorkPostedByName() {
        return workPostedByName;
    }

    public void setWorkPostedByName(String workPostedByName) {
        this.workPostedByName = workPostedByName;
    }

    public String getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    public boolean isWorkCompleted() {
        return workCompleted;
    }

    public String getWorkerPhoneNumber() {
        return workerPhoneNumber;
    }

    public void setWorkerPhoneNumber(String workerPhoneNumber) {
        this.workerPhoneNumber = workerPhoneNumber;
    }
}
