package org.example.finreport.module.dashboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlertVO implements Serializable {
    private String name;
    private Double rate;
    private String content;
    private String level;
}
