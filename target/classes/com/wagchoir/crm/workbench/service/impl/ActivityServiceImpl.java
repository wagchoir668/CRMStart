package com.wagchoir.crm.workbench.service.impl;

import com.wagchoir.crm.settings.dao.UserDao;
import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.utils.SqlSessionUtil;
import com.wagchoir.crm.vo.PaginationVO;
import com.wagchoir.crm.workbench.dao.ActivityDao;
import com.wagchoir.crm.workbench.dao.ActivityRemarkDao;
import com.wagchoir.crm.workbench.domain.Activity;
import com.wagchoir.crm.workbench.domain.ActivityRemark;
import com.wagchoir.crm.workbench.service.ActivityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao dao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao remarkDao =
            SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public List<User> selectUserList() {
        List<User> list = new ArrayList<>();
        list = dao.selectUserList();

        return list;
    }

    @Override
    public boolean saveActivity(Activity activity) {
        boolean flag = true;
        int count = dao.saveActivity(activity);
        if(count!=1) flag=false;
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        int total = dao.getTotalByCondition(map);
        List<Activity> list = dao.getActivityListByCondition(map);
        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setDataList(list);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //查出remark表中应该删除多少条记录，在remark表中删除记录
        int countShould = remarkDao.selectByIds(ids);
        int countDelete = remarkDao.deleteByIds(ids);
        if(countShould!=countDelete) flag = false;

        //在activity表中删除记录
        int count = dao.delete(ids);
        if(count!=ids.length) flag = false;

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap<>();
        List<User> uList = userDao.getUserList();
        Activity activity = dao.getActivityById(id);
        map.put("uList",uList);
        map.put("activi",activity);

        return map;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag = true;
        int count = dao.update(activity);
        if(count!=1) flag = false;
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity activity = dao.detail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkList(String id) {
        List<ActivityRemark> list = remarkDao.getRemarkList(id);
        return list;
    }

    @Override
    public boolean deleteRemarkById(String id) {
        boolean flag = true;
        int count = remarkDao.deleteRemarkById(id);
        if(count!=1) flag = false;
        return flag;
    }

    @Override
    public int saveRemark(ActivityRemark remark) {
        int count = remarkDao.saveNewRemark(remark);

        return count;
    }

    @Override
    public boolean updateRemark(ActivityRemark remark) {
        boolean flag = true;
        int count = remarkDao.updateRemark(remark);
        if(count!=1) flag = false;
        return flag;
    }

    @Override
    public List<Activity> getActivityList(String clueId) {
        List<Activity> list = dao.getActivityList(clueId);
        return list;
    }

    @Override
    public List<Activity> getActivityListByName(Map<String, String> map) {
        List<Activity> list= dao.getActivityListByName(map);
        return list;
    }

    @Override
    public List<Activity> getActivityListOnlyByName(String name) {
        List<Activity> list = dao.getActivityListOnlyByName(name);
        return list;
    }
}
