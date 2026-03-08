package com.xueqiu.etf;

import com.xueqiu.etf.backtest.BacktestService;
import com.xueqiu.etf.backtest.MinuteBacktestService;
import com.xueqiu.etf.model.ETF;
import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.ETFDataService;
import com.xueqiu.etf.service.TradeExecutor;
import com.xueqiu.etf.service.TrendAnalysisService;
import com.xueqiu.etf.service.impl.EqualWeightTradeExecutor;
import com.xueqiu.etf.service.impl.LocalETFDataService;
import com.xueqiu.etf.service.impl.MultiStrategyTrendAnalysisService;
import com.xueqiu.etf.strategy.MomentumRotationStrategy;
import com.xueqiu.etf.strategy.RotationStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        ETFDataService dataService = new LocalETFDataService();
        TrendAnalysisService trendService = new MultiStrategyTrendAnalysisService();
        RotationStrategy rotationStrategy = new MomentumRotationStrategy(15, trendService);
        TradeExecutor executor = new EqualWeightTradeExecutor();
        BacktestService backtestService = new MinuteBacktestService(dataService, rotationStrategy, executor);

        List<ETF> pool = dataService.loadETFPool("src/main/resources/etf_pool.csv");
        List<String> codes = new ArrayList<String>();
        for (ETF etf : pool) {
            codes.add(etf.getCode());
        }

        backtestService.runBacktest(codes, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1), 1_000_000);

        if (!codes.isEmpty()) {
            List<PriceData> series = dataService.fetchHistoricalData(codes.get(0), LocalDate.now().minusDays(1), LocalDate.now());
            Map<String, String> trendMap = trendService.analyzeTrend(series, "30分钟",
                    Arrays.asList("MA", "BOLLINGER", "DONCHIAN", "ADX", "MOMENTUM"));
            System.out.println("ETF: " + codes.get(0) + ", K线: 30分钟");
            for (Map.Entry<String, String> entry : trendMap.entrySet()) {
                System.out.println("  策略(" + entry.getKey() + "): " + entry.getValue());
            }
        }
    }
}
