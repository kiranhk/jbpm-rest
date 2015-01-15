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

@Path("/task")
@Produces({MediaType.APPLICATION_XML})
public class Endpoint {
	
	@Inject
	TaskService taskService;
	
	@PersistenceUnit
	EntityManagerFactory entityManagerFactory;
	
	@Inject
	UserGroupCallback userGroupCallback;

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
	@Path("/fastquery/{user}")
	public List<TaskId> getTasksQuickly(@PathParam("user") String userId) {
		
		String group = userGroupCallback.getGroupsForUser(userId, null, null).get(0);
		
		
		Query query = entityManagerFactory.createEntityManager().createQuery("select task.id from TaskImpl task where task.taskData.status = 'Ready' and '"+group+"' in elements(task.peopleAssignments.potentialOwners)");
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
		TaskId taskId = new TaskId();
		taskId.setId(ids.get(0));
		return taskId;
	}
	
	
}
