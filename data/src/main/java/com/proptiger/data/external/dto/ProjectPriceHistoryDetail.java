package com.proptiger.data.external.dto;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Model object to map cms project history
 * @author Rajeev Pandey
 *
 */
public class ProjectPriceHistoryDetail {

	private String status;
	private String message;
	private Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Map<String, Map<String, ProjectPriceDetail>>> getPrices() {
		return prices;
	}


	public void setPrices(
			Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices) {
		this.prices = prices;
	}


	public static class ProjectPriceDetail{
		private Integer project_id;
		private Integer options_id;
		private double price;
		private Date effective_date;
		private Integer phase_id;
		private String property_type;
		
		public Integer getProject_id() {
			return project_id;
		}
		public void setProject_id(Integer project_id) {
			this.project_id = project_id;
		}
		public Integer getOptions_id() {
			return options_id;
		}
		public void setOptions_id(Integer options_id) {
			this.options_id = options_id;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public Date getEffective_date() {
			return effective_date;
		}
		public void setEffective_date(Date effective_date) {
			this.effective_date = effective_date;
		}
		public Integer getPhase_id() {
			return phase_id;
		}
		public void setPhase_id(Integer phase_id) {
			this.phase_id = phase_id;
		}
		public String getProperty_type() {
			return property_type;
		}
		public void setProperty_type(String property_type) {
			this.property_type = property_type;
		}
		
	}
	
	public static void main(String[] args){
		String temp = "{\"status\":1,\"message\":\"success\",\"prices\":{\"2013-09\":{\"1197\":{\"0_207\":{\"project_id\":1197,\"options_id\":207,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_208\":{\"project_id\":1197,\"options_id\":208,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_209\":{\"project_id\":1197,\"options_id\":209,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_210\":{\"project_id\":1197,\"options_id\":210,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_211\":{\"project_id\":1197,\"options_id\":211,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_212\":{\"project_id\":1197,\"options_id\":212,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_213\":{\"project_id\":1197,\"options_id\":213,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0}}},\"2013-08\":{\"1197\":{\"0_207\":{\"project_id\":1197,\"options_id\":207,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_208\":{\"project_id\":1197,\"options_id\":208,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_209\":{\"project_id\":1197,\"options_id\":209,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_210\":{\"project_id\":1197,\"options_id\":210,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_211\":{\"project_id\":1197,\"options_id\":211,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_212\":{\"project_id\":1197,\"options_id\":212,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0},\"0_213\":{\"project_id\":1197,\"options_id\":213,\"price\":\"3250.0\",\"effective_date\":\"2013-09-01T00:00:00Z\",\"phase_id\":0}}}}}";
		ObjectMapper mapper = new ObjectMapper();
		try {
			ProjectPriceHistoryDetail history = mapper.readValue(temp, ProjectPriceHistoryDetail.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
