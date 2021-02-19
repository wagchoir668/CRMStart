package com.wagchoir.crm.workbench.service.impl;

import com.wagchoir.crm.utils.SqlSessionUtil;
import com.wagchoir.crm.workbench.dao.CustomerDao;
import com.wagchoir.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao =
            SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {
        List<String> list = customerDao.getCustomerName(name);
        return list;
    }
}
