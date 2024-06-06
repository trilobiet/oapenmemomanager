package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.CascadeType;
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
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
@EqualsAndHashCode(onlyExplicitlyIncluded=true, callSuper=false)
@Table(name = "task")
public class Task extends Auditable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_homedir", nullable = false)
	@ToString.Exclude // To String would create an infinite loop
	@NonNull
	//@JsonIgnore // Avoid back ref
	private Homedir homedir;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_script")
	private Script script;
	
	
	@JsonProperty(access = Access.READ_ONLY)
	public LocalDate getNextUpdate() {
		
		LocalDate now = LocalDate.now();
		long unitsBetween = frequency.getChronoUnit().between(startDate, now) + 1;
		LocalDate dt = startDate.plus(unitsBetween, frequency.getChronoUnit());
		return dt;
	}
	
	
	@JsonProperty(access = Access.READ_ONLY)
	public String getFrequencyAsText() {
		
		return frequency.name();
	}

	
	@JsonProperty(access = Access.READ_ONLY)
    public boolean getHasScript() {
    	
    	if ( script != null && StringUtils.hasText(script.getBody()) ) 
    		return true;
    	else 
    		return false;
    }
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinFormula("(SELECT rl.id FROM runlog rl WHERE rl.id_task = id ORDER BY rl.date DESC LIMIT 1)")
	@Setter(AccessLevel.NONE) // read only
	private RunLog latestLog;
	
}
