package org.openkoala.security.facade.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PageElementResourceDTO implements Serializable{
	
	private static final long serialVersionUID = -1461134111548409793L;
	
	private Long id;
	
	private int version;
	
	private String name;
	
	private String identifier;
	
	private String description;
	
	protected PageElementResourceDTO() {}
	
	
	public PageElementResourceDTO(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public PageElementResourceDTO(Long id, int version, String name, String identifier, String description) {
		this.id = id;
		this.version = version;
		this.name = name;
		this.identifier = identifier;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()//
				.append(name)//
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PageElementResourceDTO)) {
			return false;
		}
		PageElementResourceDTO that = (PageElementResourceDTO) other;
		return new EqualsBuilder()//
				.append(this.getName(), that.getName())//
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
				.append(getId())//
				.append(getName())//
				.append(getDescription())//
				.append(getIdentifier())//
				.build();
	}
	
}
