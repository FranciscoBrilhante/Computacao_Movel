package com.example.market.marketDatabase;

public class Report {
    String reason;
    String explain;
    int productID;
    int profileID;
    int reportID;

    public Report(String reason, String explain,int productID,int profileID,int reportID) {
        this.reason = reason;
        this.explain = explain;
        this.productID=productID;
        this.profileID=profileID;
        this.reportID=reportID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }
}
