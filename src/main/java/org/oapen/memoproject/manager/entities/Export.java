package org.oapen.memoproject.manager.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter
@ToString
@Table(name = "export")
public class Export implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private String idTask;
	@ToString.Exclude
	String content;
	private String mimetype;

}
