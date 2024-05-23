package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

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
@EqualsAndHashCode(onlyExplicitlyIncluded=true, callSuper=false)
@Table(name = "query")
public class Query extends Auditable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include
    private UUID id; 

	@Column(nullable=false)
	@NonNull
	private String name; 
	private String body, params, notes;
	
	// Queries not 'owned' by a script 
	private boolean isLibrary = false;

	// Count scripts that have a reference to this (library) query name
	@Formula("(SELECT count(s.name) FROM script s WHERE s.body LIKE CONCAT('%',name,'%') )")
	private int references;


}
