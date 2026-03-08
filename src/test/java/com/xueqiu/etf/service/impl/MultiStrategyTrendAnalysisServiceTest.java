package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.model.PriceData;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MultiStrategyTrendAnalysisServiceTest {

    private final MultiStrategyTrendAnalysisService service = new MultiStrategyTrendAnalysisService();

    @Test
    public void shouldReturnRangeWhenBarsNotEnough() {
        List<PriceData> shortList = createLinearPrices(20, 10.0, 0.1, 1000);
        Map<String, String> trend = service.analyzeTrend(shortList, "1分钟", Arrays.asList("MA", "MOMENTUM"));

        assertEquals("横盘波段", trend.get("MA"));
        assertEquals("横盘波段", trend.get("MOMENTUM"));
    }

    @Test
    public void shouldResolveMultipleStrategies() {
        List<PriceData> rising = createLinearPrices(30, 10.0, 0.3, 5000);
        Map<String, String> trend = service.analyzeTrend(rising, "1分钟", Arrays.asList("MA", "MOMENTUM", "UNKNOWN"));

        assertEquals("上涨波段", trend.get("MA"));
        assertEquals("上涨波段", trend.get("MOMENTUM"));
        assertEquals("横盘波段", trend.get("UNKNOWN"));
    }

    @Test
    public void shouldReturnDownTrendForNegativeMomentum() {
        List<PriceData> falling = createLinearPrices(30, 20.0, -0.2, 5000);
        Map<String, String> trend = service.analyzeTrend(falling, "1分钟", Arrays.asList("MOMENTUM"));

        assertEquals("下跌波段", trend.get("MOMENTUM"));
    }

    private List<PriceData> createLinearPrices(int count, double start, double step, double volume) {
        java.util.ArrayList<PriceData> list = new java.util.ArrayList<PriceData>();
        for (int i = 0; i < count; i++) {
            double close = start + i * step;
            list.add(new PriceData(
                    LocalDateTime.of(2024, 1, 1, 9, 30).plusMinutes(i),
                    close,
                    close + 0.3,
                    close - 0.3,
                    close,
                    volume
            ));
        }
        return list;
    }
}
