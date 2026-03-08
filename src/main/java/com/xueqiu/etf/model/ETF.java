package com.xueqiu.etf.model;

public class ETF {
    private String name;
    private String code;
    private String trackingIndex;
    private String industry;
    private double marketCap;

    public ETF(String name, String code, String trackingIndex, String industry, double marketCap) {
        this.name = name;
        this.code = code;
        this.trackingIndex = trackingIndex;
        this.industry = industry;
        this.marketCap = marketCap;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public String getTrackingIndex() { return trackingIndex; }
    public String getIndustry() { return industry; }
    public double getMarketCap() { return marketCap; }
}
