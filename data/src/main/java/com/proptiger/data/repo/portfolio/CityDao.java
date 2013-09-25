package com.proptiger.data.repo.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.City;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class CityDao {
	@Autowired
	private EntityManagerFactory emf;
	
	@Autowired
	private FilterQueryBuilder filterQueryBuilder;
	
	public List<City> getCities(Selector selector){
		
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		/*CriteriaQuery<City> query = builder.createQuery(City.class);
		Root<City> root = query.from(City.class);
		query.select(root);
		query.where(builder.or(builder.equal(root.get("displayPriority"), 1), builder.equal(root.get("displayPriority"), 2)));
		List<City> list = em.createQuery(query).getResultList();
		*/
		List<City> result = new ArrayList<City>();
		
		if(selector != null){
			MySqlQueryBuilder<City> mySqlQueryBuilder = new MySqlQueryBuilder<City>(builder, City.class);
			filterQueryBuilder.applyFilter(mySqlQueryBuilder, selector.getFilters(), City.class);
			SortQueryBuilder.applySort(mySqlQueryBuilder, selector.getSort(), City.class);
			result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();
			return result;
		}
		
		//-------------------
		
		return result;
	}
	
	
/*	public List<City> getCitiesUsingTuple(Selector selector){
		Set<String> fields = new HashSet<String>();
		fields.add("label");
		fields.add("displayOrder");
		fields.add("displayPriority");
		EntityManager em = emf.createEntityManager();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<Tuple> query = builder.createTupleQuery();
		
		Root<City> root = query.from(City.class);
		
		Expression<Integer> label = root.get("label");
		Expression<String> displayPriority = root.get("displayOrder");
		Expression<String> displayOrder = root.get("displayPriority");
		
		query.multiselect(label, displayOrder, displayPriority);
		query.where(builder.or(builder.equal(root.get("displayPriority"), 1), builder.equal(root.get("displayPriority"), 2)));
		
		
		TypedQuery<Tuple> list = em.createQuery(query);
		return null;
	}
*/
}
