package com.wagchoir.crm.workbench.dao;


import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.workbench.domain.Clue;

import java.util.List;

public interface ClueDao {
    List<User> selectUserList();

    int saveClue(Clue clue);

    Clue getDetail(String id);

    Clue getClueById(String clueId);

    int delete(String clueId);
}
