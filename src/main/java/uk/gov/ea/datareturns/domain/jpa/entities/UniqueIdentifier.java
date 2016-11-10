package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "unique_identifiers")
public class UniqueIdentifier implements ControlledListEntity {

    @Id
    @SequenceGenerator(name = "unique_identifiers_id_seq", sequenceName = "unique_identifiers_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unique_identifiers_id_seq")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @ManyToOne
    @JoinColumn(name="site_id")
    private Site site;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueIdentifier that = (UniqueIdentifier) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "UniqueIdentifier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", site=" + site +
                '}';
    }
}