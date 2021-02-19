package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.Contacts;
import com.wagchoir.crm.workbench.domain.ContactsRemark;

public interface ContactsDao {
    int save(Contacts contact);
}
