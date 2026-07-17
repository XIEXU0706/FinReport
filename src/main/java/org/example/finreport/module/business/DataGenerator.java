package org.example.finreport.module.business;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finreport.module.business.entity.*;
import org.example.finreport.module.business.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"dev", "dev-mysql"})
public class DataGenerator {

    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransLogRepository transLogRepository;
    private final LoanRecordRepository loanRecordRepository;
    private final ProductRepository productRepository;
    private final ProdSaleRepository prodSaleRepository;

    private final Random rnd = ThreadLocalRandom.current();
    private List<String> customerNos = new ArrayList<>();
    private List<Long> branchIds = new ArrayList<>();
    private List<String> productCodes = new ArrayList<>();

    private static final String[] CHANNELS = {"MOBILE", "ONLINE", "COUNTER", "ATM"};
    private static final String[] TRANS_TYPES = {"DEPOSIT", "WITHDRAW", "TRANSFER", "PAYMENT"};
    private static final String[] LEVELS = {"NORMAL", "SILVER", "GOLD", "PLATINUM"};
    private static final String[] SURNAMES = {"张","李","王","赵","刘","陈","杨","黄","周","吴",
            "徐","孙","马","朱","胡","郭","何","高","林","罗"};
    private static final String[] GIVEN_NAMES = {"伟","强","磊","军","勇","杰","涛","明","超","波",
            "静","丽","娟","芳","娜","敏","燕","琳","萍","玲"};

    @PostConstruct
    public void init() {
        if (customerRepository.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }
        log.info("开始生成模拟数据...");
        createBranches();
        createProducts();
        createCustomers();
        createAccounts();
        createTransLogs();
        createLoanRecords();
        createProdSales();
        log.info("模拟数据生成完成！");
    }

    private void createBranches() {
        List<Branch> branches = new ArrayList<>();
        String[][] data = {
                {"B0001","总行营业部","华北"},{"B0002","北京分行","华北"},{"B0003","上海分行","华东"},
                {"B0004","广州分行","华南"},{"B0005","成都分行","西南"},{"B0006","西安分行","西北"},
                {"B0007","深圳分行","华南"},{"B0008","杭州分行","华东"},{"B0009","南京分行","华东"},{"B0010","武汉分行","华中"}
        };
        for (String[] d : data) {
            Branch b = new Branch();
            b.setBranchCode(d[0]); b.setBranchName(d[1]); b.setRegion(d[2]); b.setStatus("ACTIVE");
            branches.add(b);
        }
        branchRepository.saveAll(branches);
        branchIds = branchRepository.findAll().stream().map(Branch::getId).toList();
        log.info("生成 {} 个支行", branchIds.size());
    }

    private void createProducts() {
        List<Product> products = new ArrayList<>();
        String[][] data = {
                // 理财产品 8个
                {"P001","安心月月盈A","FINANCIAL","LOW","2.80","1000","30"},
                {"P002","稳健季季丰B","FINANCIAL","LOW","3.20","5000","90"},
                {"P003","双季添利C","FINANCIAL","LOW","3.50","10000","180"},
                {"P004","年年宝D","FINANCIAL","LOW","3.80","10000","365"},
                {"P005","增益成长E","FINANCIAL","MEDIUM","4.20","50000","270"},
                {"P006","进取增值F","FINANCIAL","MEDIUM","4.80","100000","365"},
                {"P007","卓越回报G","FINANCIAL","HIGH","5.50","200000","540"},
                {"P008","智选尊享H","FINANCIAL","MEDIUM","4.50","50000","180"},
                // 基金产品 8个
                {"P009","货币增利宝A","FUND","LOW","2.50","100","365"},
                {"P010","短债优选B","FUND","LOW","3.00","1000","90"},
                {"P011","纯债稳健C","FUND","LOW","3.60","1000","180"},
                {"P012","混合平衡D","FUND","MEDIUM","5.00","10000","365"},
                {"P013","科创先锋E","FUND","HIGH","8.00","100000","730"},
                {"P014","消费升级F","FUND","MEDIUM","6.50","50000","365"},
                {"P015","绿色能源G","FUND","HIGH","7.20","100000","540"},
                {"P016","沪深300指增H","FUND","MEDIUM","5.80","10000","365"},
                // 保险产品 7个
                {"P017","安康重疾险A","INSURANCE","LOW","0.00","50000","365"},
                {"P018","年年分红险B","INSURANCE","LOW","3.00","10000","365"},
                {"P019","教育金保险C","INSURANCE","LOW","3.20","20000","730"},
                {"P020","养老保障D","INSURANCE","LOW","3.50","50000","1095"},
                {"P021","家庭财产险E","INSURANCE","MEDIUM","0.00","1000","365"},
                {"P022","交通意外险F","INSURANCE","LOW","0.00","500","180"},
                {"P023","健康医疗险G","INSURANCE","LOW","0.00","20000","365"},
                // 存款产品 7个
                {"P024","活期存款A","DEPOSIT","LOW","0.35","1","365"},
                {"P025","三个月定期B","DEPOSIT","LOW","1.50","1000","90"},
                {"P026","半年定期C","DEPOSIT","LOW","1.80","5000","180"},
                {"P027","一年定期D","DEPOSIT","LOW","2.00","10000","365"},
                {"P028","三年定期E","DEPOSIT","LOW","2.50","50000","1095"},
                {"P029","大额存单F","DEPOSIT","LOW","2.80","200000","365"},
                {"P030","零存整取G","DEPOSIT","LOW","1.20","1000","365"},
        };
        for (String[] d : data) {
            Product p = new Product();
            p.setProductCode(d[0]); p.setProductName(d[1]); p.setProductType(d[2]);
            p.setRiskLevel(d[3]); p.setExpectedReturn(new BigDecimal(d[4]));
            p.setMinAmount(new BigDecimal(d[5])); p.setTermDays(Integer.parseInt(d[6])); p.setStatus("SELLING");
            products.add(p);
        }
        productRepository.saveAll(products);
        productCodes = productRepository.findAll().stream().map(Product::getProductCode).toList();
        log.info("生成 {} 个产品", productCodes.size());
    }

    private void createCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            Customer c = new Customer();
            c.setCustomerNo("C" + String.format("%06d", i));
            c.setName(SURNAMES[rnd.nextInt(SURNAMES.length)] + GIVEN_NAMES[rnd.nextInt(GIVEN_NAMES.length)]);
            c.setIdType("ID_CARD");
            c.setIdNumber(String.format("%06d%04d%02d%02d%04d", rnd.nextInt(100000,999999),
                    rnd.nextInt(1960,2005), rnd.nextInt(1,13), rnd.nextInt(1,29), rnd.nextInt(1000,9999)));
            c.setGender(rnd.nextBoolean() ? "男" : "女");
            c.setPhone("138" + String.format("%08d", rnd.nextInt(100000000)));
            c.setEmail("user" + i + "@example.com");
            c.setCustomerLevel(LEVELS[rnd.nextInt(LEVELS.length)]);
            c.setBirthDate(LocalDate.of(rnd.nextInt(1960,2005), rnd.nextInt(1,13), 1).plusDays(rnd.nextInt(27)));
            c.setBranchId(branchIds.get(rnd.nextInt(branchIds.size())));
            c.setStatus("ACTIVE");
            c.setOpenDate(LocalDate.of(2026, rnd.nextInt(1,8), 1).plusDays(rnd.nextInt(28)).atTime(rnd.nextInt(9,18), rnd.nextInt(60)));
            customers.add(c);
        }
        customerRepository.saveAll(customers);
        customerNos = customerRepository.findAll().stream().map(Customer::getCustomerNo).toList();
        log.info("生成 {} 个客户", customerNos.size());
    }

    private void createAccounts() {
        List<Account> accounts = new ArrayList<>();
        int seq = 1;
        for (String cno : customerNos) {
            for (int j = 0; j < rnd.nextInt(1, 3); j++) {
                Account a = new Account();
                a.setAccountNo("A" + String.format("%06d", seq++));
                a.setCustomerNo(cno);
                a.setAccountType(new String[]{"SAVING","CHECKING","CREDIT"}[rnd.nextInt(3)]);
                a.setCurrency("CNY");
                a.setBalance(BigDecimal.valueOf(rnd.nextInt(1000, 500000)));
                a.setStatus("ACTIVE");
                accounts.add(a);
            }
        }
        accountRepository.saveAll(accounts);
        log.info("生成 {} 个账户", accounts.size());
    }

    private void createTransLogs() {
        List<TransLog> logs = new ArrayList<>();
        List<String> accountNoList = accountRepository.findAll().stream().map(Account::getAccountNo).toList();
        int[] seq = new int[]{1};
        for (int day = 0; day < 30; day++) {
            for (int i = 0; i < rnd.nextInt(100, 300); i++) {
                TransLog log = new TransLog();
                log.setTransNo("T" + String.format("%06d", seq[0]++));
                log.setAccountNo(accountNoList.get(rnd.nextInt(accountNoList.size())));
                log.setCustomerNo(customerNos.get(rnd.nextInt(customerNos.size())));
                log.setTransType(TRANS_TYPES[rnd.nextInt(TRANS_TYPES.length)]);
                log.setAmount(BigDecimal.valueOf(rnd.nextInt(100, 50000)));
                log.setFee(BigDecimal.valueOf(rnd.nextInt(0, 10)));
                log.setChannel(CHANNELS[rnd.nextInt(CHANNELS.length)]);
                log.setStatus("SUCCESS");
                log.setTransTime(LocalDateTime.of(LocalDate.of(2026,7,13).minusDays(29-day),
                        LocalTime.of(rnd.nextInt(6,23), rnd.nextInt(60), rnd.nextInt(60))));
                logs.add(log);
                if (logs.size() >= 500) { transLogRepository.saveAll(logs); logs.clear(); }
            }
        }
        if (!logs.isEmpty()) transLogRepository.saveAll(logs);
        log.info("生成 {} 条交易记录", transLogRepository.count());
    }

    private void createLoanRecords() {
        List<LoanRecord> loans = new ArrayList<>();
        BigDecimal[] REAL_AMOUNTS = {BigDecimal.valueOf(50000), BigDecimal.valueOf(80000), BigDecimal.valueOf(100000),
                BigDecimal.valueOf(150000), BigDecimal.valueOf(200000), BigDecimal.valueOf(250000),
                BigDecimal.valueOf(300000), BigDecimal.valueOf(400000), BigDecimal.valueOf(500000)};
        for (int i = 0; i < 80; i++) {
            LoanRecord l = new LoanRecord();
            l.setLoanNo("L" + String.format("%06d", i+1));
            l.setCustomerNo(customerNos.get(rnd.nextInt(customerNos.size())));
            l.setAmount(REAL_AMOUNTS[rnd.nextInt(REAL_AMOUNTS.length)]);
            l.setTermMonths(new int[]{6,12,24,36,60}[rnd.nextInt(5)]);
            l.setInterestRate(BigDecimal.valueOf(rnd.nextDouble(3.0, 6.5)).setScale(2, RoundingMode.HALF_UP));
            l.setLoanDate(LocalDate.of(2026, rnd.nextInt(1,8), 1).plusDays(rnd.nextInt(28)));
            l.setDueDate(l.getLoanDate().plusMonths(l.getTermMonths()));
            l.setRepayMethod(new String[]{"EQUAL_PRINCIPAL","EQUAL_INSTALLMENT"}[rnd.nextInt(2)]);
            double r = rnd.nextDouble();
            l.setStatus(r < 0.7 ? "NORMAL" : r < 0.85 ? "SETTLED" : r < 0.95 ? "OVERDUE" : "BAD");
            BigDecimal interest = l.getAmount().multiply(l.getInterestRate().divide(new BigDecimal("100")))
                    .multiply(BigDecimal.valueOf(l.getTermMonths()))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            l.setMaturityInterest(interest);
            l.setMaturityTotal(l.getAmount().add(interest));
            l.setRepayDate("SETTLED".equals(l.getStatus()) ? l.getDueDate() : null);
            loans.add(l);
        }
        loanRecordRepository.saveAll(loans);
        log.info("生成 {} 条贷款记录", loanRecordRepository.count());
    }

    private void createProdSales() {
        List<ProdSale> sales = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            ProdSale s = new ProdSale();
            s.setSaleNo("S" + String.format("%08d", i+1));
            s.setProductNo(productCodes.get(rnd.nextInt(productCodes.size())));
            s.setCustomerNo(customerNos.get(rnd.nextInt(customerNos.size())));
            s.setAmount(BigDecimal.valueOf(rnd.nextInt(10000, 500000)));
            s.setSaleDate(LocalDate.of(2026, rnd.nextInt(1,8), 1).plusDays(rnd.nextInt(28)));
            s.setStatus("NORMAL");
            s.setBranchId(branchIds.get(rnd.nextInt(branchIds.size())));
            sales.add(s);
        }
        prodSaleRepository.saveAll(sales);
        log.info("生成 {} 条产品销售记录", prodSaleRepository.count());
    }
}
