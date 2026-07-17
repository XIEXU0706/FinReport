package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.Branch;
import org.example.finreport.module.business.mapper.BranchMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchMapper mapper;

    public Branch getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(Branch branch) {
        // 自动生成支行编号
        String maxCode = mapper.selectMaxBranchCode();
        int next = 1;
        if (maxCode != null && maxCode.startsWith("B")) {
            next = Integer.parseInt(maxCode.substring(1)) + 1;
        }
        branch.setBranchCode("B" + String.format("%06d", next));
        if (branch.getStatus() == null) {
            branch.setStatus("ACTIVE");
        }
        mapper.insert(branch);
    }

    public void updateById(Branch branch) {
        mapper.updateById(branch);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<Branch> list() {
        return mapper.selectList();
    }

    public PageResult<Branch> page(int page, int size, String branchName, String region, String status) {
        PageParam pp = new PageParam(page, size);
        List<Branch> list = mapper.selectPage(pp.getOffset(), pp.getSize(), branchName, region, status);
        long total = mapper.selectCount(branchName, region, status);
        return PageResult.of(list, total, page, size);
    }
}
