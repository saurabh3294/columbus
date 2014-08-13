package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "FORUM_USER_TOKEN")
public class ForumUserToken  extends BaseModel{
    private static final long serialVersionUID = -4258083722571667503L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TOKEN_ID")
    private int tokenId;
    
    @Column(name = "TOKEN")
    private String token;
    
    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
}