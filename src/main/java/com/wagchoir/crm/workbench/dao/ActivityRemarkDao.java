package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int selectByIds(String[] ids);

    int deleteByIds(String[] ids);

    List<ActivityRemark> getRemarkList(String id);

    int deleteRemarkById(String id);

    int saveNewRemark(ActivityRemark remark);

    int updateRemark(ActivityRemark remark);
}
