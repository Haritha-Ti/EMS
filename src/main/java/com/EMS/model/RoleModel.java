package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="Role")
public class RoleModel extends Auditable<String> {

	@Id
	@Column(name="roleId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long roleId;
	private String roleName;
	
	
	
	public long getroleId() {
		return roleId;
	}
	public void setroleId(long roleId) {
		this.roleId = roleId;
	}
	public String getroleName() {
		return roleName;
	}
	public void setroleName(String roleName) {
		this.roleName = roleName;
	}
	public RoleModel(long roleId, String roleName) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
	}
	
	public RoleModel() {
		
	}
	
}
