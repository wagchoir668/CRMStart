package com.wagchoir.crm.workbench.web.controller;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.PrintJson;
import com.wagchoir.crm.utils.ServiceFactory;
import com.wagchoir.crm.utils.UUIDUtil;
import com.wagchoir.crm.vo.PaginationVO;
import com.wagchoir.crm.workbench.domain.*;
import com.wagchoir.crm.workbench.service.ActivityService;
import com.wagchoir.crm.workbench.service.ClueService;
import com.wagchoir.crm.workbench.service.impl.ActivityServiceImpl;
import com.wagchoir.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//市场活动控制器
public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to the clue controller!");
        String url = request.getServletPath();//url-pattern
        //根据不同的url-pattern执行不同的功能
        if("/workbench/clue/getUserList.do".equals(url))
        {
            selectUserList(request,response);
        }
        else if("/workbench/clue/saveClue.do".equals(url))
        {
            saveClue(request,response);
        }else if("/workbench/clue/detail.do".equals(url))
        {
            getDetail(request,response);
        }else if("/workbench/clue/showActivityList.do".equals(url))
        {
            getActivityList(request,response);
        }else if("/workbench/clue/unbound.do".equals(url))
        {
            unbound(request,response);
        }else if("/workbench/clue/getActivityListByNameNotClueID.do".equals(url))
        {
            getActivityListByName(request,response);
        }else if("/workbench/clue/bound.do".equals(url))
        {
            bound(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(url))
        {
            getActivityListOnlyByName(request,response);
        }else if("/workbench/clue/convert.do".equals(url))
        {
            convert(request,response);
        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        String createBy =
                ((User) request.getSession().getAttribute("user")).getId();
        Tran tran = null;
        //打一个布尔标记，后台接收到以判断转换线索是否包含交易表单
        if("y".equals(flag))//包含交易表单
        {
            tran = new Tran();
            tran.setId(UUIDUtil.getUUID());
            tran.setExpectedDate(request.getParameter("expectedDate"));
            tran.setName(request.getParameter("name"));
            tran.setMoney(request.getParameter("money"));
            tran.setStage(request.getParameter("stage"));
            tran.setActivityId(request.getParameter("activityId"));
            tran.setCreateBy(createBy);
            tran.setCreateTime(DateTimeUtil.getSysTime());
        }
        ClueService service = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        boolean flag2 = service.convert(clueId,tran,createBy);
        if(flag2){
            response.sendRedirect(request.getContextPath()+
                    "/workbench/clue/index.jsp");//重定向
        }
    }

    private void getActivityListOnlyByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list =
                service.getActivityListOnlyByName(request.getParameter("name"));
        PrintJson.printJsonObj(response,list);
    }

    private void bound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService service = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");
        boolean flag = service.bound(cid,aids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,String> map = new HashMap<>();
        map.put("name",request.getParameter("name"));
        map.put("clueId",request.getParameter("clueId"));
        List<Activity> list = service.getActivityListByName(map);
        PrintJson.printJsonObj(response,list);
    }

    private void unbound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.ubdound(request.getParameter("id"));
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list =
                service.getActivityList(request.getParameter("clueId"));
        PrintJson.printJsonObj(response,list);
    }

    private void getDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClueService service = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = service.getDetail(request.getParameter("id"));
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp")
                .forward(request,response);
    }

    //增加一条新线索
    private void saveClue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService service = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = new Clue();
        clue.setAddress(request.getParameter("address"));
        clue.setNextContactTime(request.getParameter("nextContactTime"));
        clue.setContactSummary(request.getParameter("contactSummary"));
        clue.setDescription(request.getParameter("description"));
        clue.setSource(request.getParameter("source"));
        clue.setState(request.getParameter("state"));
        clue.setMphone(request.getParameter("mphone"));
        clue.setWebsite(request.getParameter("website"));
        clue.setPhone(request.getParameter("phone"));
        clue.setEmail(request.getParameter("email"));
        clue.setJob(request.getParameter("job"));
        clue.setFullname(request.getParameter("fullname"));
        clue.setAppellation(request.getParameter("appellation"));
        clue.setCompany(request.getParameter("company"));
        clue.setOwner(request.getParameter("owner"));
        clue.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        clue.setCreateTime(DateTimeUtil.getSysTime());
        clue.setId(UUIDUtil.getUUID());

        boolean flag = service.saveClue(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void selectUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService service = (ClueService)
                ServiceFactory.getService(new ClueServiceImpl());
        List<User> list = service.selectUserList();
        PrintJson.printJsonObj(response,list);
    }
}