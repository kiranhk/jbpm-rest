package org.jbpm.examples.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jbpm.examples.bean.CustomDeploymentService;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;

@Path("/task")
@Produces({MediaType.APPLICATION_XML})
public class TaskEndpoint {
	
	@Inject 
	protected TaskService noRuntimeManagementTaskService;
	
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskEndpoint.class);
	
	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	
	@Inject
	protected UserGroupCallback userGroupCallback;
	
	@Inject
	protected CustomDeploymentService customDeploymentService;

	

	@GET
	@Path("/count/{user}")
	public Long getCount(@PathParam("user") String userId) {
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		Query query = entityManagerFactory.createEntityManager().createQuery(
				"select count(task.id) from TaskImpl task"
				+ " inner join task.peopleAssignments.potentialOwners as owners where owners.id = :groupId");
		query.setParameter("groupId", group);
		
		Long count = (Long) query.getSingleResult();
		return count;
	}
	
	@GET
	@Path("/query/{user}")
	public List<TaskId> getTasks(@PathParam("user") String userId) {
		List<Status> statuses = new ArrayList<Status>();
		statuses.add(Status.Ready);
		List<TaskSummary> tasks = noRuntimeManagementTaskService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, "en-UK");
		List<TaskId> toReturn = new ArrayList<TaskId>(tasks.size());
		for (TaskSummary taskSummary : tasks) {
			TaskId taskId = new TaskId();
			taskId.setId(taskSummary.getId());
			toReturn.add(taskId);
		}
		return toReturn;
	}
	
	@GET
	@Path("/{user}/{taskId}/complete")
	public void complete( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		customDeploymentService.getRuntimeManager(taskId);
		getTaskService(taskId).complete(taskId, userId, null);
	}

	@GET
	@Path("/{user}/{taskId}/start")
	public void start( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		getTaskService(taskId).start(taskId, userId);
	}
	
	@GET
	@Path("/{user}/{taskId}/claim")
	public void claim( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		getTaskService(taskId).claim(taskId, userId);
	}
	
	@GET
	@Path("/{user}/claim/random")
	public TaskId claimRandom( @PathParam("user") String userId) {
		TaskId task = getRandomTasks(userId);
		if(task != null) {
			LOG.info("Claiming random task ["+task.getId()+"] for user ["+userId+"]");
			getTaskService(task.getId()).claim(task.getId(), userId);
			return task;
		}
		else {
			LOG.info("No tasks to claim for user ["+userId+"]");
		}
		
		return null;
	}
	
	@GET
	@Path("/{user}/start/random")
	public TaskId startRandom( @PathParam("user") String userId) {
		TaskId taskId = getRandomTasks(userId);
		if(taskId != null) {
			LOG.info("Starting random task ["+taskId.getId()+"] for user ["+userId+"]");
			customDeploymentService.getTaskService(taskId.getId()).start(taskId.getId(), userId);
			return taskId;
		}
		else {
			LOG.info("No tasks to claim for user ["+userId+"]");
		}
		
		return null;
	}
	
	@GET
	@Path("/list/{user}/page/{page}")
	public List<TaskId> getPagedTasks(@PathParam("user") String userId, @PathParam("page") int page) {
		if(page < 1) {
			page = 1;
		}
		
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		
		Query query = entityManagerFactory.createEntityManager().createQuery("select new TaskId(task.id) from TaskImpl task "+
				" inner join task.peopleAssignments.potentialOwners as owners where where task.taskData.status = 'Ready' and owners.id = :groupId");
		query.setParameter("groupId", group);
		
		query.setMaxResults(10);
		query.setFirstResult((page-1)*10);
		
		return (List<TaskId>)query.getResultList();
	}
		
	@GET
	@Path("/random/{user}")
	public TaskId getRandomTasks(@PathParam("user") String userId) {
		Long count = getCount(userId);
		if(count <= 0) {
			//no items to get.
			return null;
		}
		
		int random = new Random().nextInt(count.intValue());
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		Query query = entityManagerFactory.createEntityManager().createQuery("select new TaskId(task.id) from TaskImpl task " +
				" inner join task.peopleAssignments.potentialOwners as owners where where task.taskData.status = 'Ready' and owners.id = :groupId");
		query.setParameter("groupId", group);
		
		query.setMaxResults(1);
		query.setFirstResult(random);
		List<TaskId> ids = (List<TaskId>)query.getResultList();
		
		if(ids.size() > 0) {
			return (ids.get(0));
		}
		
		return null;
	}
	
	protected TaskService getTaskService(Long taskId) {
		return customDeploymentService.getTaskService(taskId);
	}
}
