package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.notification.model.Subscriber;

@Entity
@Table(name = "notification.event_type_to_subscriber_mapping")
public class EventTypeToSubscriberMapping extends BaseModel{

    private static final long serialVersionUID = -6970713382337775984L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @Column(name = "event_type_id")
    private int event_type_id;
    
    @OneToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_type_id() {
        return event_type_id;
    }

    public void setEvent_type_id(int event_type_id) {
        this.event_type_id = event_type_id;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
}
