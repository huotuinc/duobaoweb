package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by lhx on 2016/3/26.
 */

@Getter
@Setter
public class WebPersonnalIssueListModel {
    List<WebIssueListModel> list;
    Integer pageNo;
    Integer totalPages;
    Long totalRecords;
}
