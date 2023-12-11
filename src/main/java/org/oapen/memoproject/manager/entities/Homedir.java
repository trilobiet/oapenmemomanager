package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Setter @Getter @ToString 
@RequiredArgsConstructor 
@NoArgsConstructor // JPA needs this 
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "homedir")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Homedir implements UserDetails, Serializable  { 

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include	
    private UUID id;	
	
	@Column(nullable = false, unique = true)
	@NonNull
	private String username; 

	@Column(nullable = false)
	@NonNull
	private String name; 

	@JsonProperty(access = Access.WRITE_ONLY) // exclude for display! 
	private String password;
	
	@Column(unique = true)
	private String accessKey;
	private boolean isEditable;
	
	private String notes;
	
	@OneToMany(mappedBy="homedir" ,fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Task> tasks;
	
	//@Formula("(SELECT COUNT(*) FROM homedir h JOIN task t ON t.id_homedir = h.id WHERE h.id = id)")
	//private int taskCount;

	// Derived properties =======================================
	
	public Integer getTaskCount() {
		if (tasks != null) return tasks.size();
		else return 0;
	}
    
    public Long getFailedTaskCount() {
		
    	if (tasks != null) return tasks.stream()
			.filter(task -> task.getLatestLog() != null)	
			.filter(task -> task.getLatestLog().isSuccess() == false)
			.count();
		
    	else return 0L;
	}
	
	// UserDetails methods =======================================

	@Override @JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptySet();
	}

	@Override @JsonIgnore
	public boolean isAccountNonExpired() { return true;	}

	@Override @JsonIgnore
	public boolean isAccountNonLocked() { return true;	}

	@Override @JsonIgnore
	public boolean isCredentialsNonExpired() { return true; }

	@Override @JsonIgnore
	public boolean isEnabled() { return true; }
	
}

