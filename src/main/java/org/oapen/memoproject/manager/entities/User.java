package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.Arrays;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "user")
public class User extends Auditable implements UserDetails, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
    private UUID id;	
	private String username, fullname;
	private boolean admin;
	@Column(updatable = false)
	private boolean editable = true;

    @JsonProperty(access = Access.WRITE_ONLY) // exclude for display! 
	private String password;	
    
    @JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		if (admin) return Arrays.asList(new SimpleGrantedAuthority("admin"));
		else return Arrays.asList(new SimpleGrantedAuthority("editor"));
	}

    @JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

    @JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

    @JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

    @JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

}
