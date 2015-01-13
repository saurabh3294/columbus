package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.model.cms.City;
import com.proptiger.core.pojo.Selector;

/**
 * @author Rajeev Pandey
 * @author Tokala Sai Teja
 * 
 */
public interface CityCustomDao {

    public List<City> getCities(Selector selector);

    public City getCity(int cityId);
}
