package soa.work.scheduler.models;

public class IndividualWork {
    private String workCategory;
    private String workDescription;
    private String workAddress;
    private String workerPhoneNumber;
    private String userPhone;
    private String createdDate;
    private String assignedTo;
    private String assignedToId;
    private String assignedAt;
    private boolean workCompleted;
    private String workDeadline;
    private String priceStartsAt;
    private boolean workAvailable;
    private String workLatitude;
    private String workLongitude;

    public IndividualWork() {
    }

    public String getWorkLatitude() {
        return workLatitude;
    }

    public void setWorkLatitude(String workLatitude) {
        this.workLatitude = workLatitude;
    }

    public String getWorkLongitude() {
        return workLongitude;
    }

    public void setWorkLongitude(String workLongitude) {
        this.workLongitude = workLongitude;
    }

    public boolean getWorkAvailable() {
        return workAvailable;
    }

    public void setWorkAvailable(boolean workAvailable) {
        this.workAvailable = workAvailable;
    }

    public String getPriceStartsAt() {
        return priceStartsAt;
    }

    public void setPriceStartsAt(String priceStartsAt) {
        this.priceStartsAt = priceStartsAt;
    }

    public String getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(String workCategory) {
        this.workCategory = workCategory;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(String assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean getWorkCompleted() {
        return workCompleted;
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

    public String getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getWorkerPhoneNumber() {
        return workerPhoneNumber;
    }

    public void setWorkerPhoneNumber(String workerPhoneNumber) {
        this.workerPhoneNumber = workerPhoneNumber;
    }

}
