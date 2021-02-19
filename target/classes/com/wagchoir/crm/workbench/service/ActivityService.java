package com.wagchoir.crm.workbench.service;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.vo.PaginationVO;
import com.wagchoir.crm.workbench.domain.Activity;
import com.wagchoir.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    List<User> selectUserList();

    boolean saveActivity(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkList(String id);

    boolean deleteRemarkById(String id);

    int saveRemark(ActivityRemark remark);

    boolean updateRemark(ActivityRemark remark);

    List<Activity> getActivityList(String clueId);

    List<Activity> getActivityListByName(Map<String, String> map);

    List<Activity> getActivityListOnlyByName(String name);
}
