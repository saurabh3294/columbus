package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "audio_attributes")
public class AudioAttributes extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    private Integer           duration         = null;          /* In seconds */

    @Column(name = "sample_rate")
    private Integer           sampleRate       = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

}
