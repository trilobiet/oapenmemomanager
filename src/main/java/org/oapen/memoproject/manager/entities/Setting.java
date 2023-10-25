package org.oapen.memoproject.manager.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter @ToString 
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "setting")
public class Setting implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@EqualsAndHashCode.Include
	@Column(name="`key`",nullable = false) // quote reserved words!
	@NonNull
	private String key;
	
	@Column(name="`value`",nullable = false) // quote reserved words!
	@NonNull
	private String value;
	
}
