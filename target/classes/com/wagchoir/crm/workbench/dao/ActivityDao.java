package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.vo.PaginationVO;
import com.wagchoir.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    List<User> selectUserList();

    Integer saveActivity(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getActivityById(String id);

    int update(Activity activity);

    Activity detail(String id);

    List<Activity> getActivityList(String clueId);

    List<Activity> getActivityListByName(Map<String, String> map);

    List<Activity> getActivityListOnlyByName(String name);
}
