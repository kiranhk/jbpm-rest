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

import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;

@Path("/task")
@Produces({MediaType.APPLICATION_XML})
public class TaskEndpoint {
	
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskEndpoint.class);
	
	@Inject
	protected TaskService taskService;
	
	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	
	@Inject
	protected UserGroupCallback userGroupCallback;

	

	@GET
	@Path("/count/{user}")
	public Long getCount(@PathParam("user") String userId) {
		
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		Query query = entityManagerFactory.createEntityManager().createQuery("select count(task.id) from TaskImpl task where task.taskData.status = 'Ready' and '"+group+"' in elements(task.peopleAssignments.potentialOwners)");
		
		Long count = (Long) query.getSingleResult();
		return count;
	}
	
	@GET
	@Path("/query/{user}")
	public List<TaskId> getTasks(@PathParam("user") String userId) {
		List<Status> statuses = new ArrayList<Status>();
		statuses.add(Status.Ready);
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, "en-UK");
		List<TaskId> toReturn = new ArrayList<TaskId>(tasks.size());
		for (TaskSummary taskSummary : tasks) {
			TaskId taskId = new TaskId();
			taskId.setId(taskSummary.getId());
			toReturn.add(taskId);
		}
		return toReturn;
	}
	
	@GET
	@Path("/{userId}/{taskId}/complete")
	public void complete( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		taskService.complete(taskId, userId, null);
	}

	@GET
	@Path("/{userId}/{taskId}/start")
	public void start( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		taskService.start(taskId, userId);
	}
	
	@GET
	@Path("/{userId}/{taskId}/claim")
	public void claim( @PathParam("user") String userId, @PathParam("taskId") Long taskId) {
		taskService.claim(taskId, userId);
	}
	
	@GET
	@Path("/{userId}/claim/random")
	public TaskId claimRandom( @PathParam("user") String userId) {
		TaskId task = getRandomTasks(userId);
		if(task != null) {
			LOG.info("Claiming random task ["+task.getId()+"] for user ["+userId+"]");
			taskService.claim(task.getId(), userId);
			return task;
		}
		else {
			LOG.info("No tasks to claim for user ["+userId+"]");
		}
		
		return null;
	}
	
	@GET
	@Path("/list/{user}/page/{page}")
	public List<TaskId> getPagedTasks(@PathParam("user") String userId, @PathParam("user") int page) {
		if(page < 1) {
			page = 1;
		}
		
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		
		Query query = entityManagerFactory.createEntityManager().createQuery("select task.id from TaskImpl task where task.taskData.status = 'Ready' and '"+group+"' in elements(task.peopleAssignments.potentialOwners)");
		query.setMaxResults(10);
		query.setFirstResult((page-1)*10);
		
		List<Long> ids = query.getResultList();
		List<TaskId> toReturn = new ArrayList<TaskId>(ids.size());
		for (Long id : ids) {
			TaskId taskId = new TaskId();
			taskId.setId(id);
			toReturn.add(taskId);
		}
		return toReturn;
	}
	
	@GET
	@Path("/random/{user}")
	public TaskId getRandomTasks(@PathParam("user") String userId) {
		
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		Query query = entityManagerFactory.createEntityManager().createQuery("select count(task.id) from TaskImpl task where task.taskData.status = 'Ready' and '"+group+"' in elements(task.peopleAssignments.potentialOwners)");
		
		Long count = (Long) query.getSingleResult();
		int random = new Random().nextInt(count.intValue());
		query = entityManagerFactory.createEntityManager().createQuery("select task.id from TaskImpl task where task.taskData.status = 'Ready' and '"+group+"' in elements(task.peopleAssignments.potentialOwners)");
		query.setMaxResults(1);
		query.setFirstResult(random);
		List<Long> ids = query.getResultList();
		
		if(ids.size() > 0) {
			TaskId taskId = new TaskId();
			taskId.setId(ids.get(0));
			return taskId;
		}
		
		return null;
	}
	
	
}
