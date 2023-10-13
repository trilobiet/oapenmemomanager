package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Setter @Getter @ToString 
@Table(name = "homedir")
public class Homedir implements Serializable { 

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	// Overbodig? @Column(name="id", columnDefinition = "VARCHAR(36)")	
    private UUID id;	
	
	private String name, username, password, notes, accessKey;
	private boolean isEditable;
	
	@OneToMany(mappedBy="homedir")
	private Set<Task> tasks;
}
