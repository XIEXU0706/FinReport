package org.example.finreport.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinReport - 银行金融报表系统")
                        .description("银行Java后端面试项目，包含经营驾驶舱、数据分析、报表中心等模块")
                        .version("1.0.0")
                        .contact(new Contact().name("FinReport")));
    }
}
