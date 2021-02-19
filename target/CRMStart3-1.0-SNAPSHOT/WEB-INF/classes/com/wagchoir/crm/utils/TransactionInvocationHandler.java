package com.wagchoir.crm.utils;

import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//这个代码一定要背，是Spring中AOP的底层原理
public class TransactionInvocationHandler implements InvocationHandler {
    private Object target;//目标类对象（被代理者）

    public TransactionInvocationHandler(Object target)
    {this.target=target;}

    //invoke是代理类的业务方法
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = null;
        Object obj = null;

        try{
            session = SqlSessionUtil.getSqlSession();
            //处理业务逻辑
            obj = method.invoke(target,args);//method是目标类的业务方法
            session.commit();//如果成功，提交事务
        }catch(Exception e)
        {
            e.printStackTrace();
            session.rollback();//失败则回滚

            throw e.getCause();//代理类处理的是什么异常，就再往上抛出该异常
        }finally {
            SqlSessionUtil.myClose(session);
        }

        return obj;
    }

    //获取代理类对象
    public Object getProxy()
    {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),this);
    }
}
