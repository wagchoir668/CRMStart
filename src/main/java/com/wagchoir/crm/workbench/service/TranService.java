package com.wagchoir.crm.workbench.service;

import com.wagchoir.crm.workbench.domain.Tran;
import com.wagchoir.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran,String customerName);

    Tran getDetail(String id);

    List<TranHistory> getHistory(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}
