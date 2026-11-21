package be.ucll.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import be.ucll.model.FlowInstance;
import be.ucll.model.enums.FlowStatus;

/** Spring Data picks this up as the implementation of {@link FlowInstanceClaims} by its name. */
class FlowInstanceClaimsImpl implements FlowInstanceClaims {

    private final MongoTemplate mongoTemplate;

    FlowInstanceClaimsImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean claimPending(String flowInstanceId) {
        Query pending = Query.query(Criteria.where("_id").is(flowInstanceId).and("flowStatus").is(FlowStatus.PENDING));
        Update activate = new Update()
                .set("flowStatus", FlowStatus.ACTIVE)
                .set("updatedAt", LocalDateTime.now())
                // Bumping the version makes any copy loaded before the claim fail its next save.
                .inc("version", 1);
        return mongoTemplate.updateFirst(pending, activate, FlowInstance.class).getModifiedCount() == 1;
    }
}
