package com.xueqiu.etf.strategy;

import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.TrendAnalysisService;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MomentumRotationStrategyTest {

    @Test
    public void shouldSelectTopNByMomentumWhenTrendIsUp() {
        TrendAnalysisService trendService = new AlwaysUpTrendService();
        MomentumRotationStrategy strategy = new MomentumRotationStrategy(2, trendService);

        Map<String, List<PriceData>> data = new HashMap<String, List<PriceData>>();
        data.put("A", prices(10, 12, 13));
        data.put("B", prices(10, 11, 15));
        data.put("C", prices(10, 10.5, 10.8));

        List<String> selected = strategy.generateSignals(data, 2);
        assertEquals(Arrays.asList("B", "A"), selected);
    }

    @Test
    public void shouldSkipEtfWhenNoStrategySaysUpTrend() {
        TrendAnalysisService trendService = new FlatTrendService();
        MomentumRotationStrategy strategy = new MomentumRotationStrategy(2, trendService);

        Map<String, List<PriceData>> data = new HashMap<String, List<PriceData>>();
        data.put("A", prices(10, 12, 13));

        List<String> selected = strategy.generateSignals(data, 3);
        assertEquals(Collections.emptyList(), selected);
    }

    private List<PriceData> prices(double c1, double c2, double c3) {
        return Arrays.asList(
                pd(0, c1),
                pd(1, c2),
                pd(2, c3)
        );
    }

    private PriceData pd(int minute, double close) {
        return new PriceData(LocalDateTime.of(2024, 1, 1, 9, 30).plusMinutes(minute), close, close + 0.2, close - 0.2, close, 1000);
    }

    private static class AlwaysUpTrendService implements TrendAnalysisService {
        @Override
        public Map<String, String> analyzeTrend(List<PriceData> priceList, String periodName, List<String> strategyList) {
            Map<String, String> map = new HashMap<String, String>();
            for (String s : strategyList) {
                map.put(s, "上涨波段");
            }
            return map;
        }
    }

    private static class FlatTrendService implements TrendAnalysisService {
        @Override
        public Map<String, String> analyzeTrend(List<PriceData> priceList, String periodName, List<String> strategyList) {
            Map<String, String> map = new HashMap<String, String>();
            for (String s : strategyList) {
                map.put(s, "横盘波段");
            }
            return map;
        }
    }
}
