package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.UUID;

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
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true) 
@Table(name = "script")
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
	
	@ManyToOne()
	@JoinColumn(name="id_query")
	private Query query;
	
	@OneToOne(optional=true, mappedBy="script")
	@ToString.Exclude
	private Task task;
	
	
}
