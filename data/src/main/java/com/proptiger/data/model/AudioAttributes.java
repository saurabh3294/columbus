package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "audio_attributes")
public class AudioAttributes extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    private int               duration;             /* In seconds */

    private String            author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
