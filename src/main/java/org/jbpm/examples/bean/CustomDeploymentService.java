package org.jbpm.examples.bean;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Task;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class CustomDeploymentService implements DeploymentService {
	private static final Logger LOG = LoggerFactory.getLogger(CustomDeploymentService.class);
	
	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	
	@Inject
	UserGroupCallback userGroupCallback;

	@Inject
	protected EntityManagerFactory emf;
	
 
    public void deploy(DeploymentUnit deploymentUnit) {
    	LOG.info("Deploy" + deploymentUnit);
    }

    
    public void undeploy(DeploymentUnit deploymentUnit) {
    	LOG.info("Undeploy" + deploymentUnit);
    }

    public RuntimeManager getRuntimeManager(Long taskId) {
    	TaskMeta meta = getTaskMeta(taskId);
    	return getRuntimeManager(meta.getDeploymentId());
    }
    
    public RuntimeManager getRuntimeManager(String s) { 
    	LOG.info("Getting custom runtime manager");
    	if (!RuntimeManagerRegistry.get().isRegistered(s)) {
    		KieServices kieServices = KieServices.Factory.get();
    		KieContainer kieContainer = kieServices.newKieContainer(new ReleaseIdImpl(s));
    		
    		
    	RuntimeEnvironment runtimeEnvironment = RuntimeEnvironmentBuilder.Factory
    			
            .get()
            .newDefaultBuilder()
            .knowledgeBase(kieContainer.getKieBase())
            .entityManagerFactory(entityManagerFactory)
            .userGroupCallback(userGroupCallback)
            .persistence(true).get();
    	 RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(runtimeEnvironment, s);
    	}
    	return RuntimeManagerRegistry.get().getManager(s);
    }
    
    public TaskService getTaskService(Long taskId) {
    	TaskMeta meta = getTaskMeta(taskId);
    	RuntimeManager runtimeManager = this.getRuntimeManager(meta.getDeploymentId());
    	return runtimeManager.getRuntimeEngine(new ProcessInstanceIdContext(meta.getProcessInstanceId())).getTaskService();
    }

	public DeployedUnit getDeployedUnit(String s) {
        return null;
    }

     
    public Collection<DeployedUnit> getDeployedUnits() {
        return null;
    }
    
    
    public TaskMeta getTaskMeta(Long taskId) {
    	Query query = entityManagerFactory.createEntityManager().createQuery("select new org.jbpm.examples.bean.TaskMeta(task.id, task.taskData.processInstanceId, task.taskData.deploymentId) from TaskImpl task where task.id = :taskId");
    	query.setParameter("taskId", taskId);
		query.setMaxResults(1);
		
		return (TaskMeta)query.getSingleResult();
    }
    
   
}