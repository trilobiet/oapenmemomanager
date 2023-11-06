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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
//@Setter(AccessLevel.NONE) // readonly
@Setter
@Getter @ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@Table(name = "runlog")
public class RunLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String message;
	private boolean isSuccess;
	private LocalDate date; 
	
	@ManyToOne()
	@JoinColumn(name = "id_task", nullable = false)
	@ToString.Exclude // To String would create an infinite loop
	@JsonIgnore // Avoids infinite loop also
	private Task task;
}
