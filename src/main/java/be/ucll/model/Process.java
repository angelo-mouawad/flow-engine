package be.ucll.model;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.exception.DomainException;

@Document(collection = "Process")
public abstract class Process {
    @Id
    private ObjectId id;
    private String title;

    public Process(String title){ 
        setTitle(title);
    }

    public abstract void execute();

    public String getId() {
        return id.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreatedAt() {
        if (id == null) 
            throw new DomainException("Process not yet Created");

        return id.getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDate();
    }
}
