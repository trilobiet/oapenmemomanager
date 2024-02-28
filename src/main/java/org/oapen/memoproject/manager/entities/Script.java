package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true) 
@Table(name = "script")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Script implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum ScriptType {
		MAIN, SNIP
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include
    private UUID id; 
    
	@Column(nullable=false, unique=true)
	@NonNull
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	@NonNull
	private ScriptType type;
	
	private String body, params, notes;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_query")
	private Query query;
	
	@OneToOne(optional=true, mappedBy="script")
	@ToString.Exclude
	@JsonBackReference // Avoid back ref
	private Task task;
	
	// Just a means to get essential task data without the infinite recursion of field task 
	@SuppressWarnings("unused")
	public Optional<Object> getTaskOutline() {
		
		if (task != null) 
			return Optional.of(new Object() {
				public UUID id = task.getId();
				public String client = task.getHomedir().getName();
				public String fileName = task.getFileName(); 
			}); 
		else 
			return Optional.empty();
	}
	
	// Count scripts that have a reference to this (library) script name
	// Underscore in like is replaced with '\_' otherwise it is read as a wildcard
	@Formula("("
		+ "SELECT count(s.name) FROM script s "
		+ "WHERE s.body LIKE CONCAT('%',REPLACE(name,'_','\\_'),'%') "
		+ "AND s.type = 'MAIN' "
		+ ")"
	)
	private int references;
	
	
}
