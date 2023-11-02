package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@NoArgsConstructor // JPA needs this 
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "query")
public class Query implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include
    private UUID id; 

	@Column(nullable=false)
	@NonNull
	private String name, body; 
	private String params, notes;

}
