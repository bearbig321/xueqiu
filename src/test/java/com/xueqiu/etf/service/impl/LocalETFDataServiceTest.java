package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.model.ETF;
import com.xueqiu.etf.model.PriceData;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocalETFDataServiceTest {

    @Test
    public void shouldFilterByMarketCapAndKeepLargestByTrackingIndex() throws Exception {
        Path csv = Files.createTempFile("etf-pool", ".csv");
        String content = "name,code,trackingIndex,industry,marketCap\n"
                + "A,510001,沪深300,宽基,400000000\n"
                + "B,510002,沪深300,宽基,800000000\n"
                + "C,510003,中证500,宽基,700000000\n"
                + "D,510004,中证500,宽基,900000000\n";
        Files.write(csv, content.getBytes(StandardCharsets.UTF_8));

        LocalETFDataService service = new LocalETFDataService();
        List<ETF> pool = service.loadETFPool(csv.toString());

        assertEquals(2, pool.size());
        assertTrue(containsCode(pool, "510002"));
        assertTrue(containsCode(pool, "510004"));
    }

    @Test
    public void shouldGenerateHistoricalAndRealtimeData() {
        LocalETFDataService service = new LocalETFDataService();
        List<PriceData> bars = service.fetchHistoricalData("510300", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));

        assertEquals(1440, bars.size());
        PriceData rt = service.fetchRealTimeData("510300");
        assertNotNull(rt.getTimestamp());
        assertTrue(rt.getVolume() > 0);
        assertFalse(Double.isNaN(rt.getClose()));
    }

    private boolean containsCode(List<ETF> pool, String code) {
        for (ETF etf : pool) {
            if (code.equals(etf.getCode())) {
                return true;
            }
        }
        return false;
    }
}
