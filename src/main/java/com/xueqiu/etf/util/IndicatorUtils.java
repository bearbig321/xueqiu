package com.xueqiu.etf.util;

import com.xueqiu.etf.model.PriceData;

import java.util.ArrayList;
import java.util.List;

public final class IndicatorUtils {
    private IndicatorUtils() {}

    public static double sma(List<PriceData> prices, int period) {
        if (period <= 0) {
            return Double.NaN;
        }
        int size = prices.size();
        if (size < period) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = size - period; i < size; i++) {
            sum += prices.get(i).getClose();
        }
        return sum / period;
    }

    public static double stdDev(List<PriceData> prices, int period) {
        double mean = sma(prices, period);
        if (Double.isNaN(mean)) {
            return Double.NaN;
        }
        int size = prices.size();
        double sum = 0;
        for (int i = size - period; i < size; i++) {
            double d = prices.get(i).getClose() - mean;
            sum += d * d;
        }
        return Math.sqrt(sum / period);
    }

    public static double momentum(List<PriceData> prices, int lookback) {
        if (lookback < 0) {
            return 0.0;
        }
        int size = prices.size();
        if (size <= lookback) {
            return 0.0;
        }
        double oldPrice = prices.get(size - lookback - 1).getClose();
        double newPrice = prices.get(size - 1).getClose();
        if (oldPrice == 0.0) {
            return 0.0;
        }
        return (newPrice - oldPrice) / oldPrice;
    }

    public static double averageVolume(List<PriceData> prices, int period) {
        if (period <= 0) {
            return Double.NaN;
        }
        int size = prices.size();
        if (size < period) {
            return Double.NaN;
        }
        double sum = 0;
        for (int i = size - period; i < size; i++) {
            sum += prices.get(i).getVolume();
        }
        return sum / period;
    }

    public static double atr(List<PriceData> prices, int period) {
        if (period <= 0) {
            return Double.NaN;
        }
        int size = prices.size();
        if (size < period + 1) {
            return Double.NaN;
        }
        List<Double> trueRanges = new ArrayList<Double>();
        for (int i = size - period; i < size; i++) {
            PriceData p = prices.get(i);
            double prevClose = prices.get(i - 1).getClose();
            double tr = Math.max(p.getHigh() - p.getLow(), Math.max(Math.abs(p.getHigh() - prevClose), Math.abs(p.getLow() - prevClose)));
            trueRanges.add(tr);
        }
        double sum = 0;
        for (Double tr : trueRanges) {
            sum += tr;
        }
        return sum / trueRanges.size();
    }
}
