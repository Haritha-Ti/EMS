package com.EMS.model;


import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.EMS.listener.ModelListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;


@Audited
@EntityListeners(ModelListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "User")
public class UserModel  extends Auditable<Long> implements UserDetails{

	@Id
	@Column(name = "userId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	
	@Column(unique = true)
	private long empId;

	@ManyToOne
	private DepartmentModel department;

	@ManyToOne
	private RoleModel role;

	@ManyToOne
	private EmployeeContractors contractor;
	
	@ManyToOne
	private Region region;
	
	@ManyToOne
	private TimeZoneModel timezone;
	
	
	@ManyToOne
	private CppLevelModel cpplevels;
	
	private String firstName, lastName,password;
	private String email;
	private String  userName;
	private long contact;
	private Date dob;
	private Date joiningDate;
	private String bloodGroup;
	private String qualification;
	private String employmentType;
	private int gender;
	private boolean active;
	private String empCategory,cppLevel,Recruiter,referredBy;
	private Date terminationDate;

	
	
	
	

//	private Address address;
	

	public CppLevelModel getCpplevels() {
		return cpplevels;
	}

	public void setCpplevels(CppLevelModel cpplevels) {
		this.cpplevels = cpplevels;
	}

	public DepartmentModel getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentModel department) {
		this.department = department;
	}

	public RoleModel getRole() {
		return role;
	}

	public void setRole(RoleModel role) {
		this.role = role;
	}

	public String getEmpCategory() {
		return empCategory;
	}

	public void setEmpCategory(String empCategory) {
		this.empCategory = empCategory;
	}

	public String getCppLevel() {
		return cppLevel;
	}

	public void setCppLevel(String cppLevel) {
		this.cppLevel = cppLevel;
	}

	public String getRecruiter() {
		return Recruiter;
	}

	public void setRecruiter(String recruiter) {
		Recruiter = recruiter;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}
	
	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(long empId) {
		this.empId = empId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getContact() {
		return contact;
	}

	public void setContact(long contact) {
		this.contact = contact;
	}
	
	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public EmployeeContractors getContractor() {
		return contractor;
	}

	public void setContractor(EmployeeContractors contractor) {
		this.contractor = contractor;
	}


	public UserModel() {

	}

	public UserModel(long userId, String firstName, String lastName, String userName, String password, String email,
			long contact,long empId, String employmentType) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.empId=empId;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.contact = contact;
		this.employmentType = employmentType;

	}

	 @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }

	    @ElementCollection(fetch = FetchType.EAGER)
	    @Builder.Default
	    private List<String> roles = new ArrayList<>();

	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
	    }

		@Override
		public String getUsername() {
			return userName;
		}

		public Region getRegion() {
			return region;
		}

		public void setRegion(Region region) {
			this.region = region;
		}

		public TimeZoneModel getTimezone() {
			return timezone;
		}

		public void setTimezone(TimeZoneModel timezone) {
			this.timezone = timezone;
		}


		

		
}
