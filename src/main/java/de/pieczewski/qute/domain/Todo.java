package de.pieczewski.qute.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Todo extends PanacheEntityBase {

    @SequenceGenerator(
            name = "todo_idx",
            allocationSize = 10,
            sequenceName = "todo_idx",
            initialValue = 10000)
    @GeneratedValue(generator = "todo_idx")
    @Id
    Long id;

    String summary;

    String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
