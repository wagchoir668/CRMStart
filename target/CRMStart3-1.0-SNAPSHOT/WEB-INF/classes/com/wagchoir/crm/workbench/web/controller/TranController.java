package com.wagchoir.crm.workbench.web.controller;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.settings.service.Impl.UserServiceImpl;
import com.wagchoir.crm.settings.service.UserService;
import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.PrintJson;
import com.wagchoir.crm.utils.ServiceFactory;
import com.wagchoir.crm.utils.UUIDUtil;
import com.wagchoir.crm.workbench.domain.Tran;
import com.wagchoir.crm.workbench.domain.TranHistory;
import com.wagchoir.crm.workbench.service.CustomerService;
import com.wagchoir.crm.workbench.service.TranService;
import com.wagchoir.crm.workbench.service.impl.CustomerServiceImpl;
import com.wagchoir.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getServletPath();//url-pattern
        //根据不同的url-pattern执行不同的功能
        if("/workbench/transaction/add.do".equals(url))
        {
            add(request,response);
        }
        else if("/workbench/transaction/getCustomerName.do".equals(url))
        {
            getCustomerName(request,response);
        }else if("/workbench/transaction/save.do".equals(url))
        {
            save(request,response);
        }else if("/workbench/transaction/detail.do".equals(url))
        {
            detail(request,response);
        }else if("/workbench/transaction/getHistory.do".equals(url))
        {
            getHistory(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(url))
        {
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(url))
        {
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService service = (TranService)
                ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = service.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy =
                ((User)request.getSession().getAttribute("user")).getName();

        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setExpectedDate(expectedDate);
        tran.setMoney(money);
        tran.setEditTime(editTime);
        tran.setEditBy(editBy);

        ServletContext application = request.getServletContext();
        Map<String,String> dicMap = (Map)application.getAttribute("pMap");
        tran.setPossibility(dicMap.get(stage));

        TranService service =
                (TranService)ServiceFactory.getService(new TranServiceImpl());
        boolean flag = service.changeStage(tran);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("tran",tran);
        PrintJson.printJsonObj(response,map);
    }

    private void getHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService service = (TranService)
                ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> historyList =
                service.getHistory(request.getParameter("tranId"));

        ServletContext application = request.getServletContext();
        Map<String,String> map = (Map)application.getAttribute("pMap");
        for(TranHistory history:historyList)
        {
            String stage = history.getStage();
            history.setPossibility(map.get(stage));
        }
        PrintJson.printJsonObj(response,historyList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TranService service = (TranService)
                ServiceFactory.getService(new TranServiceImpl());
        Tran tran = service.getDetail(request.getParameter("id"));

        ServletContext application = request.getServletContext();
        String stage = tran.getStage();
        Map<String,String> map = (Map)application.getAttribute("pMap");
        String possibility = map.get(stage);
        tran.setPossibility(possibility);

        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp")
                .forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService service = (TranService)
                ServiceFactory.getService(new TranServiceImpl());
        Tran tran = new Tran();
        String customerName = request.getParameter("customerName");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        tran.setOwner(request.getParameter("owner"));
        tran.setId(UUIDUtil.getUUID());
        tran.setContactsId(request.getParameter("contactsId"));
        tran.setMoney(request.getParameter("money"));
        tran.setName(request.getParameter("name"));
        tran.setExpectedDate(request.getParameter("expectedDate"));
        tran.setStage(request.getParameter("stage"));
        tran.setType(request.getParameter("type"));
        tran.setSource(request.getParameter("source"));
        tran.setActivityId(request.getParameter("activityId"));
        tran.setDescription(request.getParameter("description"));
        tran.setContactSummary(request.getParameter("contactSummary"));
        tran.setNextContactTime(request.getParameter("nextContactTime"));
        tran.setCreateTime(createTime);
        tran.setCreateBy(createBy);

        boolean flag = service.save(tran,customerName);
        if(flag) {
            response.sendRedirect(request.getContextPath()+
                    "/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CustomerService service = (CustomerService)
                ServiceFactory.getService(new CustomerServiceImpl());
        List<String> customerNameList = service.getCustomerName(request.getParameter("name"));
        PrintJson.printJsonObj(response,customerNameList);
    }

    //点击创建按钮，加载所有者信息，再跳转到创建页面
    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService service = (UserService)
                ServiceFactory.getService(new UserServiceImpl());
        List<User> list = service.getUserList();
        request.setAttribute("userList",list);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").
                forward(request,response);
    }
}
