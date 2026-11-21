package be.ucll.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Brings documents written by earlier versions up to date at startup. Each step only touches
 * documents that still have the old shape, so running it again does nothing.
 */
@Component
@Order(0)
public class DataMigrations implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigrations.class);

    private static final String FLOW_INSTANCES = "FlowInstance";
    private static final String SUBMISSIONS = "RequestSubmission";

    private final MongoTemplate mongoTemplate;

    public DataMigrations(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            convertApprovalMaps();
            backfillVersion(FLOW_INSTANCES);
            backfillVersion(SUBMISSIONS);
        } catch (RuntimeException e) {
            log.error("Could not migrate existing data: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Optimistic locking needs a version on every document. Without one, Spring Data would treat a
     * stored document as new and try to insert it a second time.
     */
    private void backfillVersion(String collection) {
        long updated = mongoTemplate.updateMulti(
                Query.query(Criteria.where("version").exists(false)),
                new Update().set("version", 0L),
                collection).getModifiedCount();
        if (updated > 0) {
            log.info("Added a version to {} {} documents", updated, collection);
        }
    }

    /**
     * Votes used to be a map keyed by the email with dots replaced by underscores. They are now a list
     * of {email, status}, built from the approvers on the stored request.
     */
    @SuppressWarnings("unchecked")
    private void convertApprovalMaps() {
        Query old = Query.query(Criteria.where("approvals").exists(true));
        int converted = 0;
        for (Document submission : mongoTemplate.find(old, Document.class, SUBMISSIONS)) {
            Map<String, Object> approvals = (Map<String, Object>) submission.get("approvals");
            Document request = (Document) submission.get("request");
            List<String> approvers = request == null ? List.of() : (List<String>) request.getOrDefault("approvableBy", List.of());

            List<Document> votes = new ArrayList<>();
            for (String email : approvers) {
                String normalised = email.trim().toLowerCase();
                Object status = approvals == null ? null : approvals.get(normalised.replace('.', '_'));
                if (votes.stream().noneMatch(vote -> normalised.equals(vote.get("email")))) {
                    votes.add(new Document("email", normalised).append("status", status == null ? "PENDING" : status));
                }
            }

            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(submission.get("_id"))),
                    new Update().set("votes", votes).unset("approvals"),
                    SUBMISSIONS);
            converted++;
        }
        if (converted > 0) {
            log.info("Converted the approvals of {} request submissions to the vote list", converted);
        }
    }
}
