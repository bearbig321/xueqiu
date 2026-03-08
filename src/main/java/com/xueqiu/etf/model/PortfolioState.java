package com.xueqiu.etf.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PortfolioState {
    private double cash;
    private final Map<String, Double> positions = new HashMap<String, Double>();

    public PortfolioState(double cash) {
        this.cash = cash;
    }

    public double getCash() { return cash; }
    public void setCash(double cash) { this.cash = cash; }
    public Map<String, Double> getPositions() { return Collections.unmodifiableMap(positions); }
    public void setPosition(String code, double amount) { positions.put(code, amount); }
    public void clearPositions() { positions.clear(); }
}
