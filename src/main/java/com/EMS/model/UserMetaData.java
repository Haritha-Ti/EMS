package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Audited
@EntityListeners(ModelListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "user_metadata")
public class UserMetaData extends Auditable<Long> {

	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userMetadataId;

	@Column(unique = true)
	private long empId;

	@ManyToOne
	private UserModel userId;
	
	
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

	private String firstName, lastName;
	private String email;
	private long contact;
	private Date dob;
	private Date joiningDate;
	private String bloodGroup;
	private String qualification;
	private String employmentType;
	private int gender;
	private boolean active;
	private String empCategory, cppLevel, referredBy;
	private Date terminationDate;
	@Column(name = "emailRCG")
	private String emailRCG;
	@Column(name = "maritalStatus")
	private String maritalStatus;
	@Column(name = "homeAddress")
	private String homeAddress;
	@Column(name = "cellContact")
	private String cellContact;
	@Column(name = "taxID")
	private String taxID;
	@Column(name = "recruiter")
	private String Recruiter;
	@Column(name = "employeeStatus")
	private String employeeStatus;
	public long getEmpId() {
		return empId;
	}
	public void setEmpId(long empId) {
		this.empId = empId;
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
	public EmployeeContractors getContractor() {
		return contractor;
	}
	public void setContractor(EmployeeContractors contractor) {
		this.contractor = contractor;
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
	public CppLevelModel getCpplevels() {
		return cpplevels;
	}
	public void setCpplevels(CppLevelModel cpplevels) {
		this.cpplevels = cpplevels;
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
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getJoiningDate() {
		return joiningDate;
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
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getEmploymentType() {
		return employmentType;
	}
	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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
	public String getReferredBy() {
		return referredBy;
	}
	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}
	public Date getTerminationDate() {
		return terminationDate;
	}
	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}
	public String getEmailRCG() {
		return emailRCG;
	}
	public void setEmailRCG(String emailRCG) {
		this.emailRCG = emailRCG;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	public String getCellContact() {
		return cellContact;
	}
	public void setCellContact(String cellContact) {
		this.cellContact = cellContact;
	}
	public String getTaxID() {
		return taxID;
	}
	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}
	public String getRecruiter() {
		return Recruiter;
	}
	public void setRecruiter(String recruiter) {
		Recruiter = recruiter;
	}
	public String getEmployeeStatus() {
		return employeeStatus;
	}
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
	public long getUserMetadataId() {
		return userMetadataId;
	}
	public void setUserMetadataId(long userMetadataId) {
		this.userMetadataId = userMetadataId;
	}
	public UserModel getUserId() {
		return userId;
	}
	public void setUserId(UserModel userId) {
		this.userId = userId;
	}
	
	
}
