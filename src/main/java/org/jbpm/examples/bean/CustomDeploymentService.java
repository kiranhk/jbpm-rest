package org.jbpm.examples.bean;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;


@ApplicationScoped
public class CustomDeploymentService implements DeploymentService {

 
    public void deploy(DeploymentUnit deploymentUnit) {
    }

    
    public void undeploy(DeploymentUnit deploymentUnit) {
    }

    
    public RuntimeManager getRuntimeManager(String s) {
        return null;
    }

    
    public DeployedUnit getDeployedUnit(String s) {
        return null;
    }

     
    public Collection<DeployedUnit> getDeployedUnits() {
        return null;
    }
}