package com.xueqiu.etf.service;

import java.util.List;

public interface TradeExecutor {
    void executeTrades(List<String> selectedETFs, double availableCash);
}
