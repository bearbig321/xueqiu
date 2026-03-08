package com.xueqiu.etf.service.impl;

import com.xueqiu.etf.model.ETF;
import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.ETFDataService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LocalETFDataService implements ETFDataService {
    private final Random random = new Random();

    @Override
    public List<ETF> loadETFPool(String filePath) {
        List<ETF> etfs = new ArrayList<ETF>();
        Map<String, ETF> maxByIndex = new HashMap<String, ETF>();
        try {
            Reader reader = new FileReader(filePath);
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord row : parser) {
                double marketCap = Double.parseDouble(row.get("marketCap"));
                if (marketCap <= 500000000D) {
                    continue;
                }
                ETF current = new ETF(row.get("name"), row.get("code"), row.get("trackingIndex"), row.get("industry"), marketCap);
                ETF existing = maxByIndex.get(current.getTrackingIndex());
                if (existing == null || existing.getMarketCap() < current.getMarketCap()) {
                    maxByIndex.put(current.getTrackingIndex(), current);
                }
            }
            etfs.addAll(maxByIndex.values());
        } catch (IOException e) {
            throw new IllegalStateException("加载ETF池失败: " + e.getMessage(), e);
        }
        return etfs;
    }

    @Override
    public List<PriceData> fetchHistoricalData(String etfCode, LocalDate start, LocalDate end) {
        List<PriceData> list = new ArrayList<PriceData>();
        double base = 1 + random.nextDouble();
        LocalDateTime ts = start.atStartOfDay();
        LocalDateTime endTs = end.plusDays(1).atStartOfDay();
        while (ts.isBefore(endTs)) {
            double open = base;
            double drift = (random.nextDouble() - 0.48) * 0.02;
            double close = Math.max(0.5, open * (1 + drift));
            double high = Math.max(open, close) * (1 + random.nextDouble() * 0.005);
            double low = Math.min(open, close) * (1 - random.nextDouble() * 0.005);
            double volume = 1_000_000 + random.nextDouble() * 2_000_000;
            list.add(new PriceData(ts, open, high, low, close, volume));
            base = close;
            ts = ts.plusMinutes(1);
        }
        return list;
    }

    @Override
    public PriceData fetchRealTimeData(String etfCode) {
        double price = 1 + random.nextDouble() * 2;
        return new PriceData(LocalDateTime.now(), price * 0.997, price * 1.003, price * 0.995, price, 1_000_000 + random.nextDouble() * 500_000);
    }
}
