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

    /**
     * 用途：从本地 CSV 加载 ETF 备选池，并做基础可交易性筛选。
     * 核心流程：
     * 1) 读取 CSV 并逐行解析 ETF 属性；
     * 2) 过滤市值不达标的产品（marketCap <= 5 亿）；
     * 3) 以跟踪指数为维度，仅保留市值最大的 ETF，减少同质化；
     * 4) 汇总并返回最终 ETF 池。
     * 实现方式：利用 maxByIndex 哈希表维护“每个指数当前最优 ETF”，
     * 单次遍历即可完成筛选与去重。
     */
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

    /**
     * 用途：生成指定 ETF 在给定区间内的分钟级历史行情（本地随机模拟数据）。
     * 核心流程：
     * 1) 从起始日 00:00 开始按分钟推进到结束日最后一分钟；
     * 2) 以上一分钟收盘价为下一分钟开盘基准，叠加轻微随机漂移得到 close；
     * 3) 基于 open/close 扩展 high/low，并随机生成成交量；
     * 4) 逐分钟组装为 PriceData 列表返回。
     * 实现方式：通过随机游走近似分钟波动，保证 OHLC 关系合理（high>=max(open,close)、
     * low<=min(open,close)），用于策略开发阶段的离线联调。
     */
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
