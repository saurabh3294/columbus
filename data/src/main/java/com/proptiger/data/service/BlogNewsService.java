package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.repo.BlogNewsDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class BlogNewsService {

	@Autowired
	private BlogNewsDao blogNewsDao;

	public List<WordpressPost> getBlogNewsPostsByCity(String cityName) {
		List<WordpressPost> list = blogNewsDao.findPublishedBlogNewsByCity(cityName);
		return list;
	}
}
