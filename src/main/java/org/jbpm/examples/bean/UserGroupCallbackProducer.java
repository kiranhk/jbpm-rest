package org.jbpm.examples.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.kie.api.task.UserGroupCallback;

@ApplicationScoped
public class UserGroupCallbackProducer {
	private UserGroupCallback userGroupCallback;

	@Produces
	public UserGroupCallback getUserGroupCallback() {
		if (userGroupCallback == null) {
			userGroupCallback = new CustomUserGroupCallback(true);
		}
		return userGroupCallback;
	}
}
