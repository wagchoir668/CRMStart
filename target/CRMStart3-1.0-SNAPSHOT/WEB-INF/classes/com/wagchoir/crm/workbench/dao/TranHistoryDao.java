package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {
    int save(TranHistory history);

    List<TranHistory> getHistory(String tranId);
}
