package com.proptiger.data.repo.portfolio;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.City;
import com.proptiger.data.pojo.Selector;

@Component
public class CityDao {
	@Autowired
	EntityManagerFactory emf;
	
	public List<City> getCities(Selector selector){
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<City> query = builder.createQuery(City.class);
		
		Root<City> root = query.from(City.class);
		query.select(root).where(builder.or(builder.equal(root.get("displayPriority"), 1), builder.equal(root.get("displayPriority"), 2)));
		
		List<City> list = em.createQuery(query.select(root)).getResultList();
		return list;
	}
}
