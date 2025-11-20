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
    private String id;
    private String name;
    private ProcessType processType;

    public Process(String name, ProcessType type){ 
        setName(name);
        this.processType = type;
    }

    public String getId() {
        return id.toString();
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedAt() {
        if (id == null) 
            throw new DomainException("Process not yet Created");

        return new ObjectId(id).getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDate();
    }
}
