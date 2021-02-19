package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {
    List<ClueRemark> getRemarkList(String clueId);

    int deleteRemark(ClueRemark remark);
}
