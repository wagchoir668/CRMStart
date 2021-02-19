package com.wagchoir.crm.web.filter;

import com.wagchoir.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        String url = request.getServletPath();//获取url-pattern
        if("/login.jsp".equals(url) || "/settings/user/login.do".equals(url))
        {
            chain.doFilter(req,resp);//对这两个请求直接放行
        }
        else{
            User user = (User) request.getSession().getAttribute("user");
            if(user!=null)//若user不为空，说明登录过
            {
                chain.doFilter(req,resp);//将请求放行
            }
            else//没登录过
            {
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
