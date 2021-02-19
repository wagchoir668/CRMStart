package com.wagchoir.crm.workbench.service.impl;

import com.wagchoir.crm.settings.domain.User;
import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.SqlSessionUtil;
import com.wagchoir.crm.utils.UUIDUtil;
import com.wagchoir.crm.workbench.dao.*;
import com.wagchoir.crm.workbench.domain.*;
import com.wagchoir.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao =
            SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao relationDao =
            SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao =
            SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private ContactsDao contactsDao =
            SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao =
            SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao =
            SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private CustomerDao customerDao =
            SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao =
            SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private TranDao tranDao =
            SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao =
            SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public List<User> selectUserList() {
        List<User> list = clueDao.selectUserList();
        return list;
    }

    @Override
    public boolean saveClue(Clue clue) {
        boolean flag = true;
        int count = clueDao.saveClue(clue);
        if(count!=1) flag = false;
        return flag;
    }

    @Override
    public Clue getDetail(String id) {
        Clue clue = clueDao.getDetail(id);
        return clue;
    }

    @Override
    public boolean ubdound(String id) {
        boolean flag = true;
        int count = relationDao.unbound(id);
        if(count!=1) flag = false;
        return flag;
    }

    @Override
    public boolean bound(String cid, String[] aids) {
        boolean flag = true;
        for(String aid:aids)
        {
            ClueActivityRelation relation = new ClueActivityRelation();
            relation.setActivityId(aid);
            relation.setClueId(cid);
            relation.setId(UUIDUtil.getUUID());
            int count = relationDao.bound(relation);
            if(count!=1) flag=false;
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran tran, String createBy) {
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();

        Clue clue = clueDao.getClueById(clueId);//1根据clueId拿到该条clue线索

        //2根据客户/公司名拿到客户信息,判断这条线索是否已经转化为客户
        Customer customer = customerDao.getCustomerByName(clue.getCompany());
        if(customer==null)//2若该客户不存在，则新建一个，存入表中
        {
            customer = new Customer();
            customer.setAddress(clue.getAddress());
            customer.setContactSummary(clue.getContactSummary());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setDescription(clue.getDescription());
            customer.setId(UUIDUtil.getUUID());
            customer.setName(clue.getCompany());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setOwner(clue.getOwner());
            customer.setPhone(clue.getPhone());
            customer.setWebsite(clue.getWebsite());

            int count1 = customerDao.save(customer);
            if(count1!=1) flag = false;
        }

        //3创建新的联系人，存入表中
        Contacts contact = new Contacts();
        contact.setId(UUIDUtil.getUUID());
        contact.setAddress(clue.getAddress());
        contact.setAppellation(clue.getAppellation());
        contact.setContactSummary(clue.getContactSummary());
        contact.setCreateBy(createBy);
        contact.setCreateTime(createTime);
        contact.setCustomerId(customer.getId());
        contact.setDescription(clue.getDescription());
        contact.setEmail(clue.getEmail());
        contact.setFullname(clue.getFullname());
        contact.setJob(clue.getJob());
        contact.setMphone(clue.getMphone());
        contact.setNextContactTime(clue.getNextContactTime());
        contact.setOwner(clue.getOwner());
        contact.setSource(clue.getSource());
        int count2 = contactsDao.save(contact);
        if(count2!=1) flag = false;

        //4将线索的备注转换为客户和联系人的备注
        List<ClueRemark> remarkList = clueRemarkDao.getRemarkList(clueId);
        for(ClueRemark clueRemark:remarkList)
        {
            CustomerRemark customerRemark = new CustomerRemark();//客户备注
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(clueRemark.getNoteContent());
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3!=1) flag=false;

            ContactsRemark contactsRemark = new ContactsRemark();//联系人备注
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contact.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(clueRemark.getNoteContent());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4!=1) flag=false;
        }

        //5线索和市场活动的关系 转换为 联系人和市场活动的关系
        List<ClueActivityRelation> relationList = relationDao.getActivity(clueId);
        for(ClueActivityRelation relation:relationList)
        {
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(relation.getActivityId());
            contactsActivityRelation.setContactsId(contact.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1) flag=false;
        }

        //6如果有创建交易的需求，则创建一条交易
        if(tran!=null)
        {
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contact.getId());

            int count6 = tranDao.save(tran);
            if(count6!=1) flag = false;

            //7同时创建一条交易历史
            TranHistory history = new TranHistory();
            history.setCreateBy(createBy);
            history.setCreateTime(createTime);
            history.setExpectedDate(tran.getExpectedDate());
            history.setId(UUIDUtil.getUUID());
            history.setMoney(tran.getMoney());
            history.setStage(tran.getStage());
            history.setTranId(tran.getId());

            int count7 = tranHistoryDao.save(history);
            if(count7!=1) flag = false;
        }

        //8删除该线索对应的备注
        for(ClueRemark remark:remarkList)
        {
            int count8 = clueRemarkDao.deleteRemark(remark);
            if(count8!=1) flag = false;
        }

        //9删除线索和对应市场活动之间的关系
        for(ClueActivityRelation relation:relationList)
        {
            int count9 = relationDao.delete(relation);
            if(count9!=1) flag = false;
        }

        //10删除线索
        int count10 =clueDao.delete(clueId);
        if(count10!=1) flag = false;

        return flag;
    }
}
