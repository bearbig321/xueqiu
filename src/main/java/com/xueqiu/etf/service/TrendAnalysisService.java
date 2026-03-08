package com.xueqiu.etf.service;

import com.xueqiu.etf.model.PriceData;

import java.util.List;
import java.util.Map;

public interface TrendAnalysisService {
    Map<String, String> analyzeTrend(List<PriceData> priceList, String periodName, List<String> strategyList);
}
