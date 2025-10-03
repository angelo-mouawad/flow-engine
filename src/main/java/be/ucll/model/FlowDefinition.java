package be.ucll.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;

@Table(name = "flow_definition")
@Entity
public class FlowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employee;
    private Date absenseUntil;
    private LocalTime from;
    private LocalTime until;
    private String remarks;

    public FlowDefinition() {}

    public FlowDefinition(Date absenseUntil, String employee, LocalTime from, String remarks, LocalTime until) {
        setAbsenseUntil(absenseUntil);
        setEmployee(employee);
        setFrom(from);
        setRemarks(remarks);
        setUntil(until);
    }

    public Date getAbsenseUntil() {
        return absenseUntil;
    }

    public void setAbsenseUntil(Date absenseUntil) {
        this.absenseUntil = absenseUntil;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalTime getUntil() {
        return until;
    }

    public void setUntil(LocalTime until) {
        this.until = until;
    }
}