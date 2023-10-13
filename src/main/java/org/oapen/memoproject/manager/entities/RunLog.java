package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter @ToString 
@Table(name = "runlog")
public class RunLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Integer id;
	
	String message;
	Boolean isSuccess;
	LocalDate date; 
	
	@ManyToOne()
	@JoinColumn(name = "id_task", nullable = false)
	@ToString.Exclude // To String would create an infinite loop
	Task task;
}
