package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {
    int save(Tran tran);

    Tran getDetail(String id);

    int update(Tran tran);

    int getTotalTranNumbers();

    List<Map<String, String>> getStageMap();
}
