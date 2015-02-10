package org.jbpm.examples.bean;

public class TaskMeta {
	private final Long taskId;
	private final Long processInstanceId;
	private final String deploymentId;

	public TaskMeta(Long taskId, long processInstanceId, String deploymentId) {
		this.taskId = taskId;
		this.processInstanceId = processInstanceId;
		this.deploymentId = deploymentId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

}