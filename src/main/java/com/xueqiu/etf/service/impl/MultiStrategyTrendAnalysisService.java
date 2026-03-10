package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.TrendAnalysisService;
import com.xueqiu.etf.util.IndicatorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiStrategyTrendAnalysisService implements TrendAnalysisService {

    /**
     * 用途：对同一时间序列执行多种趋势策略判定，返回“策略名 -> 趋势标签”的汇总结果。
     * 核心流程：
     * 1) 遍历传入策略列表；
     * 2) 对每个策略调用 resolveTrend 进行独立判定；
     * 3) 将结果写入 Map 统一返回给上层（例如轮动策略做共识过滤）。
     * 实现方式：采用策略名称分发（字符串路由）的轻量模式，保证调用方可按需组合策略。
     */
    @Override
    public Map<String, String> analyzeTrend(List<PriceData> priceList, String periodName, List<String> strategyList) {
        Map<String, String> result = new HashMap<String, String>();
        for (String strategy : strategyList) {
            result.put(strategy, resolveTrend(priceList, strategy));
        }
        return result;
    }

    /**
     * 用途：根据指定策略名称计算当前序列所处趋势区间（上涨/下跌/横盘）。
     * 核心流程：
     * 1) 先做最小样本判断，避免窗口指标失真；
     * 2) 按策略类型分支计算关键指标（MA、布林、唐奇安、ATR、动量/量能等）；
     * 3) 将指标值映射为统一趋势标签，便于上层组合决策。
     * 实现方式：在一个方法中集中维护“策略名 -> 指标逻辑 -> 趋势标签”的映射，
     * 通过统一返回值降低多策略结果整合成本。
     */
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
