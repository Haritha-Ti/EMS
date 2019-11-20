package com.EMS.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
@Audited
@EntityListeners(ModelListener.class)
@Entity
public class PasswordResetModel  extends  Auditable<Long>{
//	private static final int EXPIRATION = 60 * 24;
	  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  
    private String token;
  
    @OneToOne(targetEntity = UserModel.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserModel user;
  
    private Date expiryDate;
    
    @Transient
    private String status;

    public PasswordResetModel() {
    	
    }
    
    public PasswordResetModel(String token,UserModel user,Date expiryDate) {
    	this.token = token;
    	this.user = user;
    	this.expiryDate = expiryDate;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
