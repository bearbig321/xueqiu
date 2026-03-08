package com.xueqiu.etf.util;

import com.xueqiu.etf.model.PriceData;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndicatorUtilsTest {

    @Test
    public void smaAndStdDevShouldUseLatestWindow() {
        List<PriceData> prices = pricesWithCloseAndVolume(new double[]{1, 2, 3, 4, 5}, new double[]{10, 20, 30, 40, 50});

        assertEquals(4.0, IndicatorUtils.sma(prices, 2), 1e-9);
        assertEquals(Math.sqrt(0.25), IndicatorUtils.stdDev(prices, 2), 1e-9);
    }

    @Test
    public void momentumShouldHandleNormalInsufficientAndZeroBaseline() {
        List<PriceData> normal = pricesWithCloseAndVolume(new double[]{10, 11, 12}, new double[]{1, 1, 1});
        assertEquals(0.2, IndicatorUtils.momentum(normal, 2), 1e-9);

        List<PriceData> insufficient = pricesWithCloseAndVolume(new double[]{10, 11}, new double[]{1, 1});
        assertEquals(0.0, IndicatorUtils.momentum(insufficient, 2), 1e-9);

        List<PriceData> zeroBase = pricesWithCloseAndVolume(new double[]{0, 1, 2}, new double[]{1, 1, 1});
        assertEquals(0.0, IndicatorUtils.momentum(zeroBase, 2), 1e-9);
    }

    @Test
    public void averageVolumeAndAtrShouldReturnExpectedValues() {
        List<PriceData> prices = new ArrayList<PriceData>();
        prices.add(new PriceData(LocalDateTime.of(2024, 1, 1, 9, 30), 10, 10.5, 9.5, 10, 100));
        prices.add(new PriceData(LocalDateTime.of(2024, 1, 1, 9, 31), 10, 11, 9.8, 10.8, 200));
        prices.add(new PriceData(LocalDateTime.of(2024, 1, 1, 9, 32), 10.8, 11.4, 10.2, 10.5, 300));

        assertEquals(250.0, IndicatorUtils.averageVolume(prices, 2), 1e-9);
        assertEquals(1.2, IndicatorUtils.atr(prices, 2), 1e-9);
    }

    @Test
    public void invalidPeriodsShouldReturnNan() {
        List<PriceData> prices = pricesWithCloseAndVolume(new double[]{1, 2, 3}, new double[]{1, 2, 3});

        assertTrue(Double.isNaN(IndicatorUtils.sma(prices, 0)));
        assertTrue(Double.isNaN(IndicatorUtils.stdDev(prices, -1)));
        assertTrue(Double.isNaN(IndicatorUtils.averageVolume(prices, 0)));
        assertTrue(Double.isNaN(IndicatorUtils.atr(prices, 0)));
    }

    private List<PriceData> pricesWithCloseAndVolume(double[] closes, double[] volumes) {
        List<PriceData> list = new ArrayList<PriceData>();
        for (int i = 0; i < closes.length; i++) {
            double close = closes[i];
            list.add(new PriceData(LocalDateTime.of(2024, 1, 1, 9, 30).plusMinutes(i), close, close + 0.5, close - 0.5, close, volumes[i]));
        }
        return list;
    }
}
