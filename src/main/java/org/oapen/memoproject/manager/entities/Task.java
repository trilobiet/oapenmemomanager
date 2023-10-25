package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
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
@Table(name = "task")
public class Task implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum TaskFrequency {
		Y, M, W, D
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) //.SEQUENCE?
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include
    private UUID id; 
    
	@Column(nullable = false)
	@NonNull
	private String fileName, extension;
	
	private String description, notes;
	private LocalDate startDate = LocalDate.now();
	
	@Enumerated(EnumType.STRING)
	private TaskFrequency frequency = TaskFrequency.M;
	private boolean isActive, isPublic;
	
	@ManyToOne()
	@JoinColumn(name="id_homedir",nullable=false)
	@ToString.Exclude // To String would create an infinite loop
	@NonNull
	@JsonIgnore // Avoid back ref
	private Homedir homedir;
	
	@OneToOne
	@JoinColumn(name="id_script")
	private Script script;
	
	public LocalDate getNextUpdate() {
		
		LocalDate d = latestLog.getDate();

		switch (frequency) {
		
			case W: return d.plusWeeks(1);
			case M: return d.plusMonths(1);
			case Y: return d.plusYears(1);
			default: return d.plusDays(1);
		}
	}
	
	public String getFrequencyAsText() {
		
		switch (frequency) {
		
			case W: return "weekly";
			case M: return "monthly";
			case Y: return "yearly";
			default: return "daily";
		}
	}
	
	public String getPath() {
		
		return homedir.getUsername() + "/" + fileName;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinFormula("(SELECT rl.id FROM runlog rl WHERE rl.id_task = id ORDER BY rl.date DESC LIMIT 1)")
	@Setter(AccessLevel.NONE) // read only
	private RunLog latestLog;

}
