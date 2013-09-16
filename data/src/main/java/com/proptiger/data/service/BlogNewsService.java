package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.repo.BlogNewsDao;

@Service
public class BlogNewsService {

	//@Autowired
	private BlogNewsDao blogNewsDao;

	public long getBlogNews() {
		// TODO Auto-generated method stub
		long num = blogNewsDao.count();
		List<WordpressPost> list = blogNewsDao.findPostByPostTitle();
		return num;
	}
}
