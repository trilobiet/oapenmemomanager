package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter(AccessLevel.NONE) 
@Getter @ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "runlog")
public class RunLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@EqualsAndHashCode.Include
	Integer id;
	
	String message;
	Boolean isSuccess;
	LocalDate date; 
	
	@ManyToOne()
	@JoinColumn(name = "id_task", nullable = false)
	@ToString.Exclude // To String would create an infinite loop
	@JsonIgnore // Avoids infinite loop also
	Task task;
}
