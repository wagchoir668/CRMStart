package com.wagchoir.crm.settings.service;

import com.wagchoir.crm.settings.domain.DictValue;

import java.util.List;
import java.util.Map;

public interface DictService {
    Map<String, List<DictValue>> getAll();
}
