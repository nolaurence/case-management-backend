package cn.nolaurene.cms.common.dto;

import lombok.Data;

@Data
public class Pagination {

    private int current;

    private int pageSize;

    private long total;
}
