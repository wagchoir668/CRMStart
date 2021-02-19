package com.wagchoir.crm.settings.dao;

import com.wagchoir.crm.settings.domain.DictValue;

import java.util.List;

public interface DictValueDao {
    List<DictValue> getValueListByType(String code);
}
