package com.wagchoir.crm.workbench.service;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.workbench.domain.Activity;
import com.wagchoir.crm.workbench.domain.Clue;
import com.wagchoir.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    List<User> selectUserList();

    boolean saveClue(Clue clue);

    Clue getDetail(String id);

    boolean ubdound(String id);

    boolean bound(String cid, String[] aids);

    boolean convert(String clueId, Tran tran, String createBy);
}
