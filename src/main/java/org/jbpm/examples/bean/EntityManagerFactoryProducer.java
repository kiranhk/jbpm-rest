package org.jbpm.examples.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class EntityManagerFactoryProducer {
	private EntityManagerFactory entityManagerFactory;

	@Produces
	@PersistenceUnit
	public EntityManagerFactory getPersistenceUnit() {
		if (this.entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.domain");
		}
		return entityManagerFactory;
	}
}
