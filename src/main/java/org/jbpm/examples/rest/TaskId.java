package org.jbpm.examples.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="taskid")
public class TaskId {
	private Long id;

	public TaskId() {

	}
	
	public TaskId(Long id) {
		this.id = id;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
