package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter @ToString
@Table(name = "task")
public class Task implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) //.SEQUENCE?
	@Type(type="uuid-char")
    private UUID id; 
    
	private String  fileName, extension, description, frequency, notes;
	private LocalDate startDate; 
	private boolean isActive, isPublic;
	
	@ManyToOne()
	@JoinColumn(name="id_homedir",nullable=false)
	@ToString.Exclude // To String would create an infinite loop
	private Homedir homedir;
	
	@OneToOne(optional = true)
	@JoinColumn(name="id")
	private Script script;
	
	public LocalDate getNextUpdate() {
		
		LocalDate d = latestLog.getDate();

		switch (frequency) {
		
			case "W": return d.plusWeeks(1);
			case "M": return d.plusMonths(1);
			case "Y": return d.plusYears(1);
			default: return d.plusDays(1);
		}
	}
	
	public String getFrequencyAsText() {
		
		switch (frequency) {
		
			case "W": return "weekly";
			case "M": return "monthly";
			case "Y": return "yearly";
			default: return "daily";
		}
	}
	
	public String getPath() {
		
		return homedir.getUsername() + "/" + fileName;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinFormula("(SELECT rl.id FROM runlog rl WHERE rl.id_task = id ORDER BY rl.date DESC LIMIT 1)")
	private RunLog latestLog;
	
}
