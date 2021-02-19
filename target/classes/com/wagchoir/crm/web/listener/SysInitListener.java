package com.wagchoir.crm.web.listener;

import com.wagchoir.crm.settings.domain.DictValue;
import com.wagchoir.crm.settings.service.DictService;
import com.wagchoir.crm.settings.service.Impl.DictServiceImpl;
import com.wagchoir.crm.utils.ServiceFactory;
import com.wagchoir.crm.utils.SqlSessionUtil;
import com.wagchoir.crm.workbench.domain.Activity;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    //把数据字典放到服务器缓存，即application域中
    //存放的格式为{typeCode1:List<DictValue>1,typeCode2:List<DictValue>2}
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext application = sce.getServletContext();
        //类似于controller，因为同为web层三大组件filter/listener/servlet
        DictService dictService = (DictService)
                ServiceFactory.getService(new DictServiceImpl());
        //不同的type对应的多个dictValue  key为typeCode value为对应的List<DictValue>
        Map<String,List<DictValue>> dictMap = dictService.getAll();
        Set<String> keySet = dictMap.keySet();
        for(String key:keySet)
        {
            application.setAttribute(key,dictMap.get(key));
        }

        //解析properties文件
        //properties在resources文件夹下，则直接写文件名即可，不用带后缀
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> stages = rb.getKeys();//Enumeration枚举类型
        Map<String,String> map = new HashMap<>();
        while(stages.hasMoreElements())//遍历
        {
            String stage = stages.nextElement();//阶段
            String possibility = rb.getString(stage);//可能性
            map.put(stage,possibility);
        }
        application.setAttribute("pMap",map);//放入域中

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
