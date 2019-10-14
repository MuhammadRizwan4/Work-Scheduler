package soa.work.scheduler.models;

public class UserAccount {
    private String accountCreatedOn;
    private String phoneNumber;
    private String email;
    private String name;
    private String workCategory;

    public UserAccount() {
    }

    public UserAccount(String accountCreatedOn, String phoneNumber, String email, String name, String workCategory) {
        this.accountCreatedOn = accountCreatedOn;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.workCategory = workCategory;
    }

    public String getAccountCreatedOn() {
        return accountCreatedOn;
    }

    public void setAccountCreatedOn(String accountCreatedOn) {
        this.accountCreatedOn = accountCreatedOn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(String workCategory) {
        this.workCategory = workCategory;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
