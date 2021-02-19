package com.wagchoir.crm.workbench.dao;

import com.wagchoir.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {
    int unbound(String id);

    int bound(ClueActivityRelation relation);

    List<ClueActivityRelation> getActivity(String clueId);

    int delete(ClueActivityRelation relation);
}
