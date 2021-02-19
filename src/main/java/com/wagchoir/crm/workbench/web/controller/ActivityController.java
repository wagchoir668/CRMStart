package com.wagchoir.crm.workbench.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagchoir.crm.exception.LoginException;
import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.settings.service.Impl.UserServiceImpl;
import com.wagchoir.crm.settings.service.UserService;
import com.wagchoir.crm.utils.*;
import com.wagchoir.crm.vo.PaginationVO;
import com.wagchoir.crm.workbench.domain.Activity;
import com.wagchoir.crm.workbench.domain.ActivityRemark;
import com.wagchoir.crm.workbench.service.ActivityService;
import com.wagchoir.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//市场活动控制器
public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to the activity controller!");
        String url = request.getServletPath();//url-pattern
        //根据不同的url-pattern执行不同的功能
        if("/workbench/activity/getUserList.do".equals(url))
        {
            selectUserList(request,response);
        }
        else if("/workbench/activity/save.do".equals(url))
        {
            saveActivity(request,response);
        }else if("/workbench/activity/pageList.do".equals(url))
        {
            pageList(request,response);
        }else if("/workbench/activity/delete.do".equals(url))
        {
            delete(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(url))
        {
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/update.do".equals(url))
        {
            update(request,response);
        }
        else if("/workbench/activity/detail.do".equals(url))
        {
            detail(request,response);
        }else if("/workbench/activity/getRemarkList.do".equals(url))
        {
            getRemarkList(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(url))
        {
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(url))
        {
            saveRemark(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(url))
        {
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        ActivityRemark remark = new ActivityRemark();
        remark.setEditFlag("1");
        remark.setNoteContent(request.getParameter("noteContent"));
        remark.setId(request.getParameter("id"));
        remark.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        remark.setEditTime(DateTimeUtil.getSysTime());

        boolean flag = service.updateRemark(remark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("remark",remark);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        ActivityRemark remark = new ActivityRemark();
        remark.setActivityId(request.getParameter("activityId"));
        remark.setNoteContent(request.getParameter("noteContent"));
        remark.setId(UUIDUtil.getUUID());
        remark.setCreateTime(DateTimeUtil.getSysTime());
        remark.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        remark.setEditFlag("0");

        int count = service.saveRemark(remark);
        boolean flag = true;
        if(count!=1) flag = false;
        Map<String,Object> map = new HashMap<>();
        map.put("newRemark",remark);
        map.put("success",flag);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");
        boolean flag = service.deleteRemarkById(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");
        List<ActivityRemark> remarkList = service.getRemarkList(id);
        PrintJson.printJsonObj(response,remarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");
        Activity activity = service.detail(id);
        request.setAttribute("activity",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").
                forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = new Activity();
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setId(request.getParameter("id"));
        activity.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        activity.setEditTime(DateTimeUtil.getSysTime());

        boolean flag = service.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = service.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ids = request.getParameterValues("id");
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String endDate = request.getParameter("endDate");
        String startDate = request.getParameter("startDate");
        String owner = request.getParameter("owner");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("owner",owner);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        PaginationVO<Activity> vo = service.pageList(map);
        PrintJson.printJsonObj(response,vo);
    }

    private void saveActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = new Activity();
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setId(UUIDUtil.getUUID());
        activity.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        activity.setCreateTime(DateTimeUtil.getSysTime());

        boolean flag = service.saveActivity(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void selectUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        List<User> userList = service.selectUserList();
        PrintJson.printJsonObj(response,userList);
    }
}