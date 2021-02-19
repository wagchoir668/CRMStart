package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {
    Customer getCustomerByName(String company);

    int save(Customer customer);

    List<String> getCustomerName(String name);
}
