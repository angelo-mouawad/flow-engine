package be.ucll.model;

import java.time.LocalDateTime;

//Class Definition
public class FlowDefinition {

    private long id;
    private String employee;
    private LocalDateTime absenceUntil;
    private float absenceDuration;
    private LocalDateTime from;
    private LocalDateTime until;
    private String remarks;
    private Status status;

    public enum Status {
        PENDING,
        IN_PROGRESS,
        APPROVED,
        DENIED
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public void setAbsenceUntil(LocalDateTime absenceUntil) {
        this.absenceUntil = absenceUntil;
    }

    public void setAbsenceDuration(float absenceDuration) {
        this.absenceDuration = absenceDuration;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public void setUntil(LocalDateTime until) {
        this.until = until;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public float getAbsenceDuration() {
        return absenceDuration;
    }

    public String getEmployee() {
        return employee;
    }

    public LocalDateTime getAbsenceUntil() {
        return absenceUntil;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getUntil() {
        return until;
    }

    public Status getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public long getId() {
        return id;
    }

    //Constructor
    public FlowDefinition(long id, String employee, LocalDateTime absenceUntil,
                          float absenceDuration, LocalDateTime from, LocalDateTime until,
                          String remarks, Status status) {
        this.id = id;
        this.employee = employee;
        this.absenceUntil = absenceUntil;
        this.absenceDuration = absenceDuration;
        this.from = from;
        this.until = until;
        this.remarks = remarks;
        this.status = status;
    }


}