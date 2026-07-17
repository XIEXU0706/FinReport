package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.Branch;
import java.util.List;

public interface BranchMapper {

    Branch selectById(Long id);

    List<Branch> selectList();

    int insert(Branch branch);

    int updateById(Branch branch);

    int deleteById(Long id);

    List<Branch> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                            @Param("branchName") String branchName,
                            @Param("region") String region,
                            @Param("status") String status);

    long selectCount(@Param("branchName") String branchName,
                     @Param("region") String region,
                     @Param("status") String status);

    String selectMaxBranchCode();
}
