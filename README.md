# ETF轮动系统（Java 8）

基于控制台的ETF轮动系统骨架，支持：

- ETF池CSV加载、过滤（市值>5亿、同指数取最大市值）
- 分钟级历史/实时行情接口（当前示例使用本地模拟数据）
- 动量轮动策略（可叠加趋势过滤）
- 多策略K线趋势判断（MA/Bollinger/Donchian/ADX/Momentum）
- 分钟级回测骨架与控制台输出
- 可扩展调度与CSV报表输出

## 运行

```bash
mvn -q clean package
java -cp target/etf-rotation-system-1.0.0.jar com.xueqiu.etf.App
```

## 模块

- `model`: ETF与K线数据结构
- `service`: 数据、趋势分析、交易执行接口
- `strategy`: 轮动策略接口与实现
- `backtest`: 回测服务
- `scheduler`: 分钟级调度
- `output`: CSV输出
