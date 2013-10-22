package com.proptiger.data.model.portfolio;

import java.util.Date;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo
public class HelpdeskTicket {

	@FieldMetaInfo(displayName = "Helpdesk Ticket Id", description = "Helpdesk Ticket Id")
	private long id;
	
	@FieldMetaInfo(displayName = "Date", description = "Date")
	private Date date;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "Ticket Type", description = "Ticket Type")
	private TicketType ticketType;
	
	@FieldMetaInfo(displayName = "Description", description = "Description")
	private String description;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "Status", description = "Status")
	private TicketStatus status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TicketType getTicketType() {
		return ticketType;
	}

	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}
	
	
}
