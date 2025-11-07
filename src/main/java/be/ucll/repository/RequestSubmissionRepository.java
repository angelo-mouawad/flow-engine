package be.ucll.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.RequestStatus;
import be.ucll.model.RequestSubmission;

public interface RequestSubmissionRepository extends MongoRepository<RequestSubmission, ObjectId>{
    public List<RequestSubmission> findByStatus(RequestStatus status);
    public List<RequestSubmission> findByFlowInstanceId(ObjectId flowInstanceId);
}
