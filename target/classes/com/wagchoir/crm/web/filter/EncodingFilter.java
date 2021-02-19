package com.wagchoir.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        //针对post请求中午乱码问题
        req.setCharacterEncoding("UTF-8");
        //对于响应中的中文乱码问题
        resp.setContentType("text/html;charset=utf-8");

        filterChain.doFilter(req,resp);//将请求放行
    }

    @Override
    public void destroy() {

    }
}
