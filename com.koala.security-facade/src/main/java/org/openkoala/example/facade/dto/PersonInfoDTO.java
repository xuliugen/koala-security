package org.openkoala.example.facade.dto;

import java.util.Date;
import java.io.Serializable;

public class PersonInfoDTO implements Serializable {

	private static final long serialVersionUID = 1428864474382580128L;

	private Long id;
	
	private int version;

	private Double proceeds;
	
	private Date birthday;
	
	private Date birthdayEnd;
		
	private String sex;
	
	private Boolean married;
	
    private String name;
	
	private String identityCardNumber;
	
	public void setProceeds(Double proceeds) { 
		this.proceeds = proceeds;
	}

	public Double getProceeds() {
		return this.proceeds;
	}

	public void setBirthday(Date birthday) { 
		this.birthday = birthday;
	}

	public Date getBirthday() {
		return this.birthday;
	}
	
	public void setBirthdayEnd(Date birthdayEnd) { 
		this.birthdayEnd = birthdayEnd;
	}

	public Date getBirthdayEnd() {
		return this.birthdayEnd;
	}

	public void setSex(String sex) { 
		this.sex = sex;
	}

	public String getSex() {
		return this.sex;
	}

	public void setMarried(Boolean married) { 
		this.married = married;
	}

	public Boolean getMarried() {
		return this.married;
	}

	public void setName(String name) { 
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setIdentityCardNumber(String identityCardNumber) { 
		this.identityCardNumber = identityCardNumber;
	}

	public String getIdentityCardNumber() {
		return this.identityCardNumber;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

    public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonInfoDTO other = (PersonInfoDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}