package com.wagchoir.workbench.test;

import com.wagchoir.crm.utils.ServiceFactory;
import com.wagchoir.crm.utils.UUIDUtil;
import com.wagchoir.crm.workbench.domain.Activity;
import com.wagchoir.crm.workbench.service.ActivityService;
import com.wagchoir.crm.workbench.service.impl.ActivityServiceImpl;
import org.junit.Test;

public class ActivityTest {
    @Test
    public void TestSave()
    {
        System.out.println("111");
    }

    @Test
    public void TestUpdate()
    {
        System.out.println("222");
    }

    @Test
    public void f2(){
        System.out.println(3/1);
    }

    @Test
    public void testActivity()
    {
        Activity activity = new Activity();
        activity.setId(UUIDUtil.getUUID());
        activity.setName("cxy");
        ActivityService service = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.saveActivity(activity);
        System.out.println(flag);
    }
}
