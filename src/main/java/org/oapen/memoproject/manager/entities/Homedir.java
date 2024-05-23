package org.oapen.memoproject.manager.entities;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
@Table(name = "homedir")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Homedir extends Auditable implements Serializable  { 

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type="uuid-char")
	@EqualsAndHashCode.Include	
    private UUID id;	
	
	@Column(nullable = false, unique = true)
	@NonNull
	private String username;
	
	@Column(nullable = false)
	@NonNull
	private String name; 

	@JsonProperty(access = Access.WRITE_ONLY) // exclude for display! 
	private String password;
	
	@Column(unique = true, nullable = false)
	private String accessKey;
	private boolean isEditable;
	
	private String notes;
	
	@OneToMany(mappedBy="homedir" ,fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Task> tasks;
	
	//@Formula("(SELECT COUNT(*) FROM homedir h JOIN task t ON t.id_homedir = h.id WHERE h.id = id)")
	//private int taskCount;

	// Derived properties =======================================
	
	public Integer getTaskCount() {
		if (tasks != null) return tasks.size();
		else return 0;
	}
    
    public Long getFailedTaskCount() {
		
    	if (tasks != null) return tasks.stream()
			.filter(task -> task.getLatestLog() != null)	
			.filter(task -> !task.getLatestLog().isSuccess())
			.count();
		
    	else return 0L;
	}

    public Long getPassedTaskCount() {
		
    	if (tasks != null) return tasks.stream()
			.filter(task -> task.getLatestLog() != null)	
			.filter(task -> task.getLatestLog().isSuccess())
			.count();
		
    	else return 0L;
	}
    
    public Long getEmptyScriptsCount() {
    	
    	if (tasks != null) return tasks.stream()
    		.filter(task -> 
    			task.getScript() == null 
    			|| !StringUtils.hasText(task.getScript().getBody()) 
    		)
    		.count();
    	
    	else return 0L;
    }
	
}

