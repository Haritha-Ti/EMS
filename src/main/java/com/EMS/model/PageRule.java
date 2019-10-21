package com.EMS.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAGERULE")
public class PageRule {

	@Id
	@Column(name = "page_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "page_key")
	private String pageKey;

	@Column(name = "role_id")
	private long roleId;


	private long parent_Id;
	
	private long level_Id;
	
	private String path;
	
	private String icon;
	
	private String label;
	
	@Column(name = "sort")
	private int sort;
	
	@Column(name = "menu")
	private boolean menu;
	
	
	

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public boolean isMenu() {
		return menu;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getParent_Id() {
		return parent_Id;
	}

	public void setParent_Id(long parent_Id) {
		this.parent_Id = parent_Id;
	}

	public long getLevel_Id() {
		return level_Id;
	}

	public void setLevel_Id(long level_Id) {
		this.level_Id = level_Id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPageKey() {
		return pageKey;
	}

	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}



	
}
