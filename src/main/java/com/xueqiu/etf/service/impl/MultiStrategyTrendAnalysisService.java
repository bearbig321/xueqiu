package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.TrendAnalysisService;
import com.xueqiu.etf.util.IndicatorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiStrategyTrendAnalysisService implements TrendAnalysisService {

    @Override
    public Map<String, String> analyzeTrend(List<PriceData> priceList, String periodName, List<String> strategyList) {
        Map<String, String> result = new HashMap<String, String>();
        for (String strategy : strategyList) {
            result.put(strategy, resolveTrend(priceList, strategy));
        }
        return result;
    }

    private String resolveTrend(List<PriceData> priceList, String strategy) {
        if (priceList.size() < 25) {
            return "横盘波段";
        }
        if ("MA".equalsIgnoreCase(strategy) || "EMA".equalsIgnoreCase(strategy)) {
            double fast = IndicatorUtils.sma(priceList, 5);
            double slow = IndicatorUtils.sma(priceList, 20);
            return compare(fast, slow, 0.001);
        }
        if ("BOLLINGER".equalsIgnoreCase(strategy)) {
            double mid = IndicatorUtils.sma(priceList, 20);
            double std = IndicatorUtils.stdDev(priceList, 20);
            double close = priceList.get(priceList.size() - 1).getClose();
            if (close > mid + 2 * std) return "上涨波段";
            if (close < mid - 2 * std) return "下跌波段";
            return "横盘波段";
        }
        if ("DONCHIAN".equalsIgnoreCase(strategy) || "RANGE".equalsIgnoreCase(strategy)) {
            double high = Double.NEGATIVE_INFINITY;
            double low = Double.POSITIVE_INFINITY;
            for (int i = priceList.size() - 20; i < priceList.size(); i++) {
                high = Math.max(high, priceList.get(i).getHigh());
                low = Math.min(low, priceList.get(i).getLow());
            }
            double close = priceList.get(priceList.size() - 1).getClose();
            if (close >= high) return "上涨波段";
            if (close <= low) return "下跌波段";
            return "横盘波段";
        }
        if ("ADX".equalsIgnoreCase(strategy) || "RSI".equalsIgnoreCase(strategy)) {
            double atr = IndicatorUtils.atr(priceList, 14);
            double ratio = atr / priceList.get(priceList.size() - 1).getClose();
            if (ratio > 0.015) return "上涨波段";
            if (ratio < 0.005) return "横盘波段";
            return "下跌波段";
        }
        if ("MOMENTUM".equalsIgnoreCase(strategy) || "ROC".equalsIgnoreCase(strategy) || "VOLUME".equalsIgnoreCase(strategy)) {
            double m = IndicatorUtils.momentum(priceList, 10);
            double avgVol = IndicatorUtils.averageVolume(priceList, 10);
            double nowVol = priceList.get(priceList.size() - 1).getVolume();
            if (m > 0 && nowVol >= avgVol) return "上涨波段";
            if (m < 0) return "下跌波段";
            return "横盘波段";
        }
        return "横盘波段";
    }

    private String compare(double a, double b, double threshold) {
        if (a - b > threshold) return "上涨波段";
        if (b - a > threshold) return "下跌波段";
        return "横盘波段";
    }
}
