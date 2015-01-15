package org.jbpm.examples.bean;

import javax.enterprise.inject.Produces;

import org.jbpm.services.task.identity.DefaultUserInfo;
import org.kie.internal.task.api.UserInfo;

public class UserInfoProducer {
	
	@Produces
	public UserInfo getUserInfo() {
		return new DefaultUserInfo(true);
	}
}
