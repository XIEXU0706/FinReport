package org.example.finreport.module.dashboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopBranchVO implements Serializable {
    private String branchName;
    private BigDecimal tradeAmount;
}
