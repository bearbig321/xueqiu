package com.xueqiu.etf.strategy;

import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.TrendAnalysisService;
import com.xueqiu.etf.util.IndicatorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomentumRotationStrategy implements RotationStrategy {
    private final int lookbackBars;
    private final TrendAnalysisService trendService;

    public MomentumRotationStrategy(int lookbackBars, TrendAnalysisService trendService) {
        this.lookbackBars = lookbackBars;
        this.trendService = trendService;
    }

    @Override
    public List<String> generateSignals(Map<String, List<PriceData>> etfPriceMap, int topN) {
        Map<String, Double> scoreMap = new HashMap<String, Double>();
        for (Map.Entry<String, List<PriceData>> entry : etfPriceMap.entrySet()) {
            List<PriceData> prices = entry.getValue();
            if (prices == null || prices.isEmpty()) {
                continue;
            }
            Map<String, String> trend = trendService.analyzeTrend(prices, "1分钟", java.util.Arrays.asList("MA", "ADX", "MOMENTUM"));
            boolean trendOk = false;
            for (String v : trend.values()) {
                if ("上涨波段".equals(v)) {
                    trendOk = true;
                    break;
                }
            }
            if (!trendOk) {
                continue;
            }
            scoreMap.put(entry.getKey(), IndicatorUtils.momentum(prices, lookbackBars));
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(scoreMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return Double.compare(o2.getValue(), o1.getValue());
            }
        });

        List<String> selected = new ArrayList<String>();
        for (int i = 0; i < Math.min(topN, entries.size()); i++) {
            selected.add(entries.get(i).getKey());
        }
        return selected;
    }
}
