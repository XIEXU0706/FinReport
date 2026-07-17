package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.TransLog;
import java.time.LocalDateTime;
import java.util.List;

public interface TransLogMapper {

    TransLog selectById(Long id);

    List<TransLog> selectList();

    int insert(TransLog log);

    int updateById(TransLog log);

    int deleteById(Long id);

    List<TransLog> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("transType") String transType,
                              @Param("channel") String channel,
                              @Param("dateFrom") LocalDateTime dateFrom,
                              @Param("dateTo") LocalDateTime dateTo,
                              @Param("customerNo") String customerNo,
                              @Param("accountNo") String accountNo);

    long selectCount(@Param("transType") String transType,
                     @Param("channel") String channel,
                     @Param("dateFrom") LocalDateTime dateFrom,
                     @Param("dateTo") LocalDateTime dateTo,
                     @Param("customerNo") String customerNo,
                     @Param("accountNo") String accountNo);
}
