package be.ucll.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;

public interface RequestSubmissionRepository extends MongoRepository<RequestSubmission, String> {

    List<RequestSubmission> findByStatus(RequestStatus status);

    List<RequestSubmission> findByFlowInstanceId(String flowInstanceId);

    void deleteByFlowInstanceId(String flowInstanceId);
}
