package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	String notes;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	// UserDetails methods =======================================
		
	@Override @JsonIgnore
	public boolean isAccountNonExpired() { return true;	}

	@Override @JsonIgnore
	public boolean isAccountNonLocked() { return true;	}

	@Override @JsonIgnore
	public boolean isCredentialsNonExpired() { return true; }

	@Override @JsonIgnore
	public boolean isEnabled() { return true; }
	
}
