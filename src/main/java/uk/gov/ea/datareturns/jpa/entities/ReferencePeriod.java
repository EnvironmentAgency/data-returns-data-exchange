package uk.gov.ea.datareturns.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@Entity
@Table(name = "reference_periods")
@NamedQuery(name = "ReferencePeriod.findAll", query = "SELECT r FROM ReferencePeriod r")
public class ReferencePeriod {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public ReferencePeriod() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}