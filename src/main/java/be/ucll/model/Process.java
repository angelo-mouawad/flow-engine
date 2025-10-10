package be.ucll.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Process")
public abstract class Process {
    @Id
    public String id;
}
