package com.xueqiu.etf.backtest;

import java.time.LocalDate;
import java.util.List;

public interface BacktestService {
    void runBacktest(List<String> etfPool, LocalDate start, LocalDate end, double initialCash);
}
