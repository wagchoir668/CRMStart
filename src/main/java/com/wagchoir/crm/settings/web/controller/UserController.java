package com.wagchoir.crm.settings.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagchoir.crm.exception.LoginException;
import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.settings.service.Impl.UserServiceImpl;
import com.wagchoir.crm.settings.service.UserService;
import com.wagchoir.crm.utils.MD5Util;
import com.wagchoir.crm.utils.PrintJson;
import com.wagchoir.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to the user controller!");
        String url = request.getServletPath();//url-pattern
        //根据不同的url-pattern执行不同的功能
        if("/settings/user/login.do".equals(url))
        {
            login(request,response);
        }
        else if("/settings/user/bbb.do".equals(url))
        {
            //
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String account = request.getParameter("loginAct");//从前端获取的账号密码
        String pwd = request.getParameter("loginPwd");
        pwd = MD5Util.getMD5(pwd);//转为MD5加密形式
        String ip = request.getRemoteAddr();//获取ip地址

        //将这三个参数传给service层   使用动态代理
        UserService service = (UserService)
                ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = service.login(account,pwd,ip);
            //如果没有抛出异常，说明登陆成功，即各项验证都通过
            request.getSession().setAttribute("user",user);
            //使用工具类，将字符串转为json对象
            PrintJson.printJsonFlag(response,true);
        }catch(LoginException e){ //若抛出异常，则说明业务层验证登录失败
            e.printStackTrace();

            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("errorMsg",e.getMessage());

            PrintJson.printJsonObj(response,map);
        }
    }
}
