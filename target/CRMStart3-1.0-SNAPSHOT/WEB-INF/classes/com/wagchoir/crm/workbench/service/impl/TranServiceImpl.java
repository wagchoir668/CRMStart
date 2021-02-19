package com.wagchoir.crm.workbench.service.impl;

import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.SqlSessionUtil;
import com.wagchoir.crm.utils.UUIDUtil;
import com.wagchoir.crm.workbench.dao.CustomerDao;
import com.wagchoir.crm.workbench.dao.TranDao;
import com.wagchoir.crm.workbench.dao.TranHistoryDao;
import com.wagchoir.crm.workbench.domain.Customer;
import com.wagchoir.crm.workbench.domain.Tran;
import com.wagchoir.crm.workbench.domain.TranHistory;
import com.wagchoir.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao =
            SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao =
            SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao =
            SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran,String customerName) {
        boolean flag = true;
        //先根据customerName获取customer，若该customer不存在就新建一个
        Customer customer = customerDao.getCustomerByName(customerName);
        if(customer==null)
        {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setCreateBy(tran.getCreateBy());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());

            int count1 = customerDao.save(customer);
            if(count1!=1) flag = false;
        }

        tran.setCustomerId(customer.getId());//把tran中缺失的内容放入
        int count2 = tranDao.save(tran);//把这条新的交易记录存入交易表中
        if(count2!=1) flag = false;

        //添加新的交易历史
        TranHistory history = new TranHistory();
        history.setId(UUIDUtil.getUUID());
        history.setTranId(tran.getId());
        history.setStage(tran.getStage());
        history.setMoney(tran.getMoney());
        history.setExpectedDate(tran.getExpectedDate());
        history.setCreateTime(DateTimeUtil.getSysTime());
        history.setCreateBy(tran.getCreateBy());
        int count3 = tranHistoryDao.save(history);
        if(count3!=1) flag = false;

        return flag;
    }

    @Override
    public Tran getDetail(String id) {
        Tran tran = tranDao.getDetail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistory(String tranId) {
        List<TranHistory> list = tranHistoryDao.getHistory(tranId);
        return list;
    }

    @Override
    public boolean changeStage(Tran tran) {
        boolean flag = true;
        int count1 = tranDao.update(tran);
        if(count1!=1) flag = false;

        TranHistory history = new TranHistory();
        history.setId(UUIDUtil.getUUID());
        history.setTranId(tran.getId());
        history.setStage(tran.getStage());
        history.setMoney(tran.getMoney());
        history.setExpectedDate(tran.getExpectedDate());
        history.setCreateTime(DateTimeUtil.getSysTime());
        history.setCreateBy(tran.getEditBy());
        int count2 = tranHistoryDao.save(history);
        if(count2!=1) flag = false;

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        Map<String,Object> map = new HashMap<>();

        int total = tranDao.getTotalTranNumbers();
        map.put("total",total);

        List<Map<String,String>> stageMap = tranDao.getStageMap();
        map.put("dataList",stageMap);

        return map;
    }
}
