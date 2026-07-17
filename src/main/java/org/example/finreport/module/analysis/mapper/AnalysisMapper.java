package org.example.finreport.module.analysis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AnalysisMapper {

    /** 交易分析 - 汇总统计 */
    Map<String, Object> selectTradeSummary(@Param("days") int days);
    /** 交易分析 - 日趋势 */
    List<Map<String, Object>> selectTradeDailyTrend(@Param("days") int days);
    /** 交易分析 - 渠道占比 */
    List<Map<String, Object>> selectTradeChannelRatio(@Param("days") int days);
    /** 交易分析 - 近24小时分布 */
    List<Map<String, Object>> selectTradeHourlyDist24h();
    /** 交易分析 - 大额交易 */
    List<Map<String, Object>> selectLargeTrades(@Param("minAmount") double minAmount, @Param("days") int days);
    /** 交易分析 - 交易类型分布 */
    List<Map<String, Object>> selectTradeTypeDist(@Param("days") int days);

    /** 客户分析 - 汇总 */
    Map<String, Object> selectCustomerSummary();
    /** 客户分析 - 等级分布 */
    List<Map<String, Object>> selectCustomerLevelDist();
    /** 客户分析 - 月新增趋势 */
    List<Map<String, Object>> selectCustomerMonthlyGrowth(@Param("months") int months);
    /** 客户分析 - 年龄结构 */
    List<Map<String, Object>> selectCustomerAgeDist();
    /** 客户分析 - 地区分布 */
    List<Map<String, Object>> selectCustomerRegionDist();

    /** 贷款分析 - 汇总 */
    Map<String, Object> selectLoanSummary();
    /** 贷款分析 - 状态分布 */
    List<Map<String, Object>> selectLoanStatusDist();
    /** 贷款分析 - 月趋势 */
    List<Map<String, Object>> selectLoanMonthlyTrend(@Param("months") int months);
    /** 贷款分析 - 期限分布 */
    List<Map<String, Object>> selectLoanTermDist();
    /** 贷款分析 - 逾期分析 */
    List<Map<String, Object>> selectLoanOverdueAnalysis();

    /** 产品分析 - 销量排行 */
    List<Map<String, Object>> selectProductSalesRank();
    /** 产品分析 - 类型分布 */
    List<Map<String, Object>> selectProductTypeDist();
    /** 产品分析 - 收益率排行 */
    List<Map<String, Object>> selectProductReturnRank();
}
