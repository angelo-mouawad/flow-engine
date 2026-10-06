package be.ucll.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.ucll.controller.dto.FlowData;
import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.model.Approval;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.Notification;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

class JsonContractTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final String FRONTEND_PAYLOAD = """
            {
              "title": "Absence",
              "description": "Report an absence",
              "triggerableBy": ["lais@glackit.be"],
              "processes": [
                {"processType": "REQUEST", "name": "Absence", "requestTypeName": "ABSENCE_REQUEST",
                 "approvable": true, "minApprovals": 1, "approvableBy": ["adam@glackit.be"], "step": 0},
                {"processType": "APPROVAL", "name": "Approval", "requestSteps": [0], "step": 1},
                {"processType": "NOTIFICATION", "name": "Notify", "notificationType": "POPUP_NOTIFICATION",
                 "toNotify": [], "requestStep": 0, "step": 2}
              ]
            }
            """;

    @Test
    void readsTheFlowDefinitionTheFrontendSends() throws Exception {
        FlowDefinitionInput input = mapper.readValue(FRONTEND_PAYLOAD, FlowDefinitionInput.class);

        Request request = assertInstanceOf(Request.class, input.processes().get(0));
        Approval approval = assertInstanceOf(Approval.class, input.processes().get(1));
        Notification notification = assertInstanceOf(Notification.class, input.processes().get(2));

        assertEquals(RequestTypeEnum.ABSENCE_REQUEST, request.getRequestTypeName());
        assertEquals(List.of("adam@glackit.be"), request.getApprovableBy());
        assertEquals(Set.of(0), approval.getRequestSteps());
        assertEquals(NotificationTypeEnum.POPUP_NOTIFICATION, notification.getNotificationType());

        FlowDefinition definition = new FlowDefinition(input.title(), input.description(),
                input.triggerableBy(), input.processes());
        assertEquals(3, definition.getProcesses().size());
    }

    @Test
    void writesADefinitionTheFrontendCanReadBack() throws Exception {
        FlowDefinitionInput input = mapper.readValue(FRONTEND_PAYLOAD, FlowDefinitionInput.class);
        FlowDefinition definition = new FlowDefinition(input.title(), input.description(),
                input.triggerableBy(), input.processes());

        JsonNode json = mapper.valueToTree(definition);

        assertEquals("REQUEST", json.at("/processes/0/processType").asText());
        assertEquals("ABSENCE_REQUEST", json.at("/processes/0/requestTypeName").asText());
        assertEquals(0, json.at("/processes/1/requestSteps/0").asInt());
        assertEquals("POPUP_NOTIFICATION", json.at("/processes/2/notificationType").asText());
        assertFalse(json.get("anyTrigger").asBoolean());
        assertFalse(json.at("/processes/0").has("requestType"));
        assertFalse(json.at("/processes/2").has("strategy"));

        FlowDefinitionInput roundTrip = mapper.treeToValue(json, FlowDefinitionInput.class);
        assertEquals(3, roundTrip.processes().size());
    }

    @Test
    void writesInstancesAndSubmissionsWithoutInternalFields() {
        FlowDefinition definition = new FlowDefinition("t", "d", Set.of(),
                List.of(new Request("Absence", RequestTypeEnum.ABSENCE_REQUEST, true, List.of("a.b@glackit.be"), 1),
                        new Approval("Approval", Set.of(0))));
        FlowInstance instance = new FlowInstance(definition, "Run", "lais@glackit.be", Map.of(), null);
        RequestSubmission submission = new RequestSubmission(
                new RequestData(Map.of("Reason", "sick")),
                (Request) definition.getProcesses().get(0), "flow-1", RequestStatus.PENDING);

        JsonNode instanceJson = mapper.valueToTree(instance);
        assertEquals("ACTIVE", instanceJson.get("flowStatus").asText());
        assertEquals(2, instanceJson.get("processes").size());
        assertEquals("REQUEST", instanceJson.at("/currentProcess/processType").asText());

        JsonNode submissionJson = mapper.valueToTree(submission);
        assertEquals("sick", submissionJson.at("/data/allFields/Reason").asText());
        assertEquals("PENDING", submissionJson.at("/approvals/a.b@glackit.be").asText());
        assertEquals("flow-1", submissionJson.get("flowInstanceId").asText());
        assertFalse(submissionJson.has("request"));
        assertTrue(submissionJson.has("submittedAt"));
    }

    @Test
    void readsATriggerWithAndWithoutCallback() throws Exception {
        FlowData withoutCallback = mapper.readValue("""
                {"title": "Run", "triggeredBy": "lais@glackit.be",
                 "data": {"ABSENCE_REQUEST": {"Reason": "sick"}}}
                """, FlowData.class);
        FlowData withCallback = mapper.readValue("""
                {"title": "Run", "triggeredBy": "lais@glackit.be", "data": {}, "callbackUrl": "http://x.test"}
                """, FlowData.class);

        assertEquals("sick", withoutCallback.data().get(RequestTypeEnum.ABSENCE_REQUEST).get("Reason"));
        assertEquals(null, withoutCallback.callbackUrl());
        assertEquals("http://x.test", withCallback.callbackUrl());
    }

    @Test
    void requestWithoutTypeIsRejectedWithItsDomainMessage() {
        String payload = FRONTEND_PAYLOAD.replace("\"requestTypeName\": \"ABSENCE_REQUEST\",", "");

        JsonMappingException error = assertThrows(JsonMappingException.class,
                () -> mapper.readValue(payload, FlowDefinitionInput.class));

        Throwable cause = error;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        assertEquals("Every request process needs a request type", cause.getMessage());
    }
}
