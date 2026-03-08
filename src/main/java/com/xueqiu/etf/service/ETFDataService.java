package com.xueqiu.etf.service;

import com.xueqiu.etf.model.ETF;
import com.xueqiu.etf.model.PriceData;

import java.time.LocalDate;
import java.util.List;

public interface ETFDataService {
    List<ETF> loadETFPool(String filePath);
    List<PriceData> fetchHistoricalData(String etfCode, LocalDate start, LocalDate end);
    PriceData fetchRealTimeData(String etfCode);
}
