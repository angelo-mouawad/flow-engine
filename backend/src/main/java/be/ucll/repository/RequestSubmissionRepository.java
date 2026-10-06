package be.ucll.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.enums.RequestStatus;
import be.ucll.model.RequestSubmission;

public interface RequestSubmissionRepository extends MongoRepository<RequestSubmission, String>{
    public List<RequestSubmission> findByStatus(RequestStatus status);
    public List<RequestSubmission> findByFlowInstanceId(String flowInstanceId);
}
