package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 由于filter中不提供通配符解析，所以引入工具类
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取本次请求的URL
        String requestURI = request.getRequestURI();
        // 2.判断URL是否需要处理
        // 2.1 定义直接放行的URL
        // 这里不拦截对于html的请求，因为静态页面里面也没有什么内容
        // 主要用来拦截对controller的请求
        String[] excludeUrl = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        // 3.如果不需要处理则直接放行
        if(check(requestURI, excludeUrl)) {
            // 不需要处理 直接放行
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 4.判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee") != null) {
            // 用户已经登录 放行
            BaseContext.put((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 判断移动端用户是否已经登录
        if(request.getSession().getAttribute("user") != null) {
            // 用户已经登录 放行
            BaseContext.put((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 5.如果没有登录，则向前端返回错误代码
        // 这里使用流的方式是因为不是在Controller中 不能直接返回，其次doFilter方法返回值是void
        // 这里直接访问/backend/index.html 会首先打开这个html页面，但是由于这个页面打开的时候会请求controller
        // 访问controller就会被过滤器拦截，如果没有登录，则直接跳转到登录页面
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    // 判断当前请求是否在排除的url中
    private boolean check(String requestUrl, String[] excludeUrl) {
        for(String s : excludeUrl) {
            if(PATH_MATCHER.match(s, requestUrl)) {
                return true;
            }
        }
        return false;
    }
}
