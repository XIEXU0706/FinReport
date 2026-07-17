package org.example.finreport.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long pageSize;
    private long pages;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int pageSize) {
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(records, total, page, pageSize, pages);
    }
}
