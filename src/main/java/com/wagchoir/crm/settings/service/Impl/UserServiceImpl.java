package com.wagchoir.crm.settings.service.Impl;

import com.wagchoir.crm.exception.LoginException;
import com.wagchoir.crm.settings.dao.UserDao;
import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.settings.service.UserService;
import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    UserDao dao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String account, String pwd, String ip) throws LoginException {
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",account);
        map.put("loginPwd",pwd);

        User user = dao.login(map);
        if(user==null)
        {
            throw new LoginException("账号或密码错误");
        }

        //账号密码正确，接着验证其他三个
        String expireTime = user.getExpireTime();
        String nowTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(nowTime)<0)
        {
            throw new LoginException("账号已经失效");
        }

        String state = user.getLockState();
        if("0".equals(state))
        {
            throw new LoginException("账号已经被锁底");
        }

        String ips = user.getAllowIps();
        if(!ips.contains(ip))
        {
            throw new LoginException("不允许该IP地址访问");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> list = dao.getUserList();
        return list;
    }
}
