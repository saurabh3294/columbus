package com.proptiger.data.event.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.proptiger.data.event.model.EventGenerated;

/**
 * 
 * @author sahil
 *
 */
public class EventGeneratedDaoImpl implements EventGeneratedDao {
	
	@Override
	public Integer getEventCountByEventStatus(EventGenerated.EventStatus eventStatus) {
		// TODO Auto-generated method stub
		return null;		
	}

	@Override
	public List<EventGenerated> findByStatusOrderByCreatedDateAsc(String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<EventGenerated> findAll(Sort arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<EventGenerated> findAll(Pageable arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Integer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(EventGenerated arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends EventGenerated> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exists(Integer arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<EventGenerated> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<EventGenerated> findAll(Iterable<Integer> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventGenerated findOne(Integer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventGenerated> S save(S arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventGenerated> Iterable<S> save(Iterable<S> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
