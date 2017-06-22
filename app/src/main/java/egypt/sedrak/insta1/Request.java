package egypt.sedrak.insta1;

import java.io.Serializable;

/**
 * Created by lenovov on 01-Mar-17.
 */
public class Request implements Serializable {




    private Package mPackage;
    private String from;
    private String to;
    private String expire_date;
    private String event_date;
    private String name;
    private String telephoneNumber;
    private String address;
    private String status;
    private String packageId;


    public Request(){

    }

    public Request(String from, String to, String expire_date, String event_date, String categorie,
                   String price, String name, String telephoneNumber, String address, String status){
        this.from = from;
        this.to = to;
        this.expire_date = expire_date;
        this.event_date = event_date;
        this.name = name;
        this.telephoneNumber = telephoneNumber;
        this.address = address;
        this.status = status;
    }

    public Package getmPackage() {
        return mPackage;
    }

    public void setmPackage(Package mPackage) {
        this.mPackage = mPackage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

}
