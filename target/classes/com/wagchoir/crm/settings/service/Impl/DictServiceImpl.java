package com.wagchoir.crm.settings.service.Impl;

import com.wagchoir.crm.settings.dao.DictTypeDao;
import com.wagchoir.crm.settings.dao.DictValueDao;
import com.wagchoir.crm.settings.domain.DictType;
import com.wagchoir.crm.settings.domain.DictValue;
import com.wagchoir.crm.settings.service.DictService;
import com.wagchoir.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictServiceImpl implements DictService {
    private DictTypeDao typeDao =
            SqlSessionUtil.getSqlSession().getMapper(DictTypeDao.class);
    private DictValueDao valueDao =
            SqlSessionUtil.getSqlSession().getMapper(DictValueDao.class);

    @Override
    public Map<String, List<DictValue>> getAll() {
        Map<String, List<DictValue>> map = new HashMap<>();
        List<DictType> dictTypeList = typeDao.getTypeList();
        for(DictType dictType:dictTypeList)
        {
            List<DictValue> dictValueList =
                    valueDao.getValueListByType(dictType.getCode());
            map.put(dictType.getCode(),dictValueList);
        }

        return map;
    }
}
