package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notification")
public class Notification extends Process {

}