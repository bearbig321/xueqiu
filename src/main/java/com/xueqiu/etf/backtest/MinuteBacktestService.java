package com.xueqiu.etf.backtest;

import com.xueqiu.etf.model.PriceData;
import com.xueqiu.etf.service.ETFDataService;
import com.xueqiu.etf.service.TradeExecutor;
import com.xueqiu.etf.strategy.RotationStrategy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinuteBacktestService implements BacktestService {
    private final ETFDataService dataService;
    private final RotationStrategy strategy;
    private final TradeExecutor tradeExecutor;

    public MinuteBacktestService(ETFDataService dataService, RotationStrategy strategy, TradeExecutor tradeExecutor) {
        this.dataService = dataService;
        this.strategy = strategy;
        this.tradeExecutor = tradeExecutor;
    }

    /**
     * 用途：执行一次分钟级轮动回测，串联“取数→选基→交易→输出结果”完整链路。
     * 核心流程：
     * 1) 拉取池内每只 ETF 在区间内的分钟历史数据并聚合到 allData；
     * 2) 调用轮动策略生成候选持仓（最多 3 只）；
     * 3) 将候选持仓与初始资金交给交易执行器，模拟调仓；
     * 4) 输出回测绩效指标（当前为示例指标占位，便于流程联调）。
     * 实现方式：通过 ETFDataService、RotationStrategy、TradeExecutor 三个抽象协作，
     * 将回测流程解耦为可替换组件，便于后续替换真实撮合与绩效计算逻辑。
     */
    @Override
    public void runBacktest(List<String> etfPool, LocalDate start, LocalDate end, double initialCash) {
        Map<String, List<PriceData>> allData = new HashMap<String, List<PriceData>>();
        for (String code : etfPool) {
            allData.put(code, dataService.fetchHistoricalData(code, start, end));
        }
        List<String> selected = strategy.generateSignals(allData, Math.min(3, etfPool.size()));
        tradeExecutor.executeTrades(selected, initialCash);

        double fakeCumulative = 0.08;
        double annualized = 0.12;
        double maxDrawdown = 0.05;
        double winRate = 0.58;
        System.out.println("[回测结果] 累计收益=" + fakeCumulative + " 年化=" + annualized
                + " 最大回撤=" + maxDrawdown + " 胜率=" + winRate);
    }
}
