package com.nokia.mid.iapinfo;

public class AccessPoint
{
    private int iAccessPointID;
    private String iAccessPointName = null;
    private String iAccessPointBearerType = null;
    private String iAccessPointServiceType = "";
    static final String PARAM_NOKIA_APNID = "nokia_apnid";

    AccessPoint(int aAccessPointID, String aAccessPointName, String aAccessPointBearerType, String aAccessPointServiceType)
    {
        this.iAccessPointID = aAccessPointID;
        this.iAccessPointName = aAccessPointName;
        this.iAccessPointBearerType = aAccessPointBearerType;
        this.iAccessPointServiceType = aAccessPointServiceType;
    }

    public String getServiceType()
    {
        return this.iAccessPointServiceType;
    }

    public String getBearerType()
    {
        return this.iAccessPointBearerType;
    }

    public int getID()
    {
        return this.iAccessPointID;
    }

    public String getName()
    {
        return this.iAccessPointName;
    }

    public String getURL(String aURL)
    {
        return aURL + ";" + "nokia_apnid" + "=" + this.iAccessPointID;
    }
}
