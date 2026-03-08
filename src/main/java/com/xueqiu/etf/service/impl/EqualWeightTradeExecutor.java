package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.service.TradeExecutor;

import java.util.List;

public class EqualWeightTradeExecutor implements TradeExecutor {
    @Override
    public void executeTrades(List<String> selectedETFs, double availableCash) {
        if (selectedETFs == null || selectedETFs.isEmpty()) {
            System.out.println("无可交易ETF，保持现金仓位");
            return;
        }
        double cashPerETF = availableCash / selectedETFs.size();
        System.out.println("[交易执行] 等权分配开始");
        for (String code : selectedETFs) {
            System.out.println("  BUY " + code + " 金额=" + String.format("%.2f", cashPerETF));
        }
    }
}
