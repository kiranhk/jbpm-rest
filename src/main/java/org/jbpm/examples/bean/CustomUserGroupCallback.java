package org.jbpm.examples.bean;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.task.UserGroupCallback;

public class CustomUserGroupCallback implements UserGroupCallback{
	
	public CustomUserGroupCallback(boolean active) {
		
	}

	public boolean existsUser(String userId) {
		return true;
	}

	public boolean existsGroup(String groupId) {
		return true;
	}

	public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
		List<String> toReturn = new ArrayList<String>();
		String group = null;
		
		if (userId.startsWith("user")) {
			try {
				Integer identifier = (Integer.parseInt(userId.substring(4)) - 1) % 5 + 1;
				group = "step" + identifier;
				toReturn.add(group);
			} catch (NumberFormatException e) {
				
			}
		}
		return toReturn;
	}

}
