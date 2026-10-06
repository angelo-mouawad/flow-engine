package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.exception.DomainException;

@Document(collection = "FlowDefinition")
public class FlowDefinition {

    @Id
    private String id;
    private String title;
    private String description;
    private List<Process> processes = new ArrayList<>();
    private Set<String> triggerableBy = new LinkedHashSet<>();
    private LocalDateTime updatedAt;

    @PersistenceCreator
    protected FlowDefinition() {
    }

    public FlowDefinition(String title, String description, Set<String> triggerableBy, List<? extends Process> processes) {
        setTitle(title);
        setDescription(description);
        setTriggerableBy(triggerableBy);
        setProcesses(processes);
        touch();
    }

    public boolean canBeTriggeredBy(String user) {
        if (isAnyTrigger()) {
            return true;
        }
        return user != null && triggerableBy.contains(user.trim().toLowerCase());
    }

    public void touch() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTriggerableBy() {
        return Set.copyOf(triggerableBy);
    }

    public boolean isAnyTrigger() {
        return triggerableBy.isEmpty();
    }

    public List<Process> getProcesses() {
        return List.copyOf(processes);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        if (id == null || !ObjectId.isValid(id)) {
            return updatedAt;
        }
        return LocalDateTime.ofInstant(new ObjectId(id).getDate().toInstant(), ZoneOffset.UTC);
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Flow definition title cannot be blank");
        }
        this.title = title.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DomainException("Flow definition description cannot be blank");
        }
        this.description = description.trim();
    }

    public void setTriggerableBy(Set<String> triggerableBy) {
        this.triggerableBy = new LinkedHashSet<>(Request.normaliseEmails(
                triggerableBy == null ? null : new ArrayList<>(triggerableBy)));
    }

    public void setProcesses(List<? extends Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new DomainException("A flow definition needs at least one process");
        }

        List<Process> ordered = new ArrayList<>(processes.size());
        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.get(i);
            if (process == null) {
                throw new DomainException("Process at step " + i + " is empty");
            }
            process.setStep(i);
            ordered.add(process);
        }

        for (Process process : ordered) {
            validateProcess(process, ordered);
        }
        this.processes = ordered;
    }

    private static void validateProcess(Process process, List<Process> all) {
        if (process.getName() == null || process.getName().isBlank()) {
            throw new DomainException("Process at step " + process.getStep() + " needs a name");
        }

        switch (process) {
            case Request request -> request.validate();
            case Approval approval -> validateApproval(approval, all);
            case Notification notification -> validateNotification(notification, all);
            default -> throw new DomainException("Unknown process type at step " + process.getStep());
        }
    }

    private static void validateApproval(Approval approval, List<Process> all) {
        if (approval.getRequestSteps().isEmpty()) {
            throw new DomainException("Approval " + approval.getLabel() + " must point at at least one request");
        }
        for (int target : approval.getRequestSteps()) {
            boolean valid = target >= 0
                    && target < approval.getStep()
                    && all.get(target) instanceof Request request
                    && request.isApprovable();
            if (!valid) {
                throw new DomainException("Approval " + approval.getLabel() + " points at step " + target
                        + ", which is not an earlier request that needs approval");
            }
        }
    }

    private static void validateNotification(Notification notification, List<Process> all) {
        Integer target = notification.getRequestStep();
        Request request = null;

        if (target != null) {
            boolean valid = target >= 0
                    && target < notification.getStep()
                    && all.get(target) instanceof Request;
            if (!valid) {
                throw new DomainException("Notification " + notification.getLabel() + " points at step " + target
                        + ", which is not an earlier request");
            }
            request = (Request) all.get(target);
        }

        if (notification.recipientsFor(request).isEmpty()) {
            throw new DomainException("Notification " + notification.getLabel() + " has nobody to notify");
        }
    }
}
