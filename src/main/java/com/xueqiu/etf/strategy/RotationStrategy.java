package com.xueqiu.etf.strategy;

import com.xueqiu.etf.model.PriceData;

import java.util.List;
import java.util.Map;

public interface RotationStrategy {
    List<String> generateSignals(Map<String, List<PriceData>> etfPriceMap, int topN);
}
