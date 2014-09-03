package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;

@Service
public class ForumUserService {
    @Autowired
    private ForumUserDao forumUserDao;
    
    public ForumUser findOne(Integer userId){
        return forumUserDao.findOne(userId);
    }

}
