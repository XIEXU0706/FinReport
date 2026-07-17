package org.example.finreport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@MapperScan({"org.example.finreport.module.business.mapper",
             "org.example.finreport.module.system.mapper",
             "org.example.finreport.module.dashboard.mapper",
             "org.example.finreport.module.analysis.mapper",
             "org.example.finreport.module.report.mapper",
             "org.example.finreport.module.alert.mapper"})
public class FinReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinReportApplication.class, args);
    }

}
