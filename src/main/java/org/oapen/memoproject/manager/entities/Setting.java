package org.oapen.memoproject.manager.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter @ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "settings")
public class Setting implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@EqualsAndHashCode.Include
	private String key;
	
	@Column(nullable = false)
	@NonNull
	private String value;
	
}
