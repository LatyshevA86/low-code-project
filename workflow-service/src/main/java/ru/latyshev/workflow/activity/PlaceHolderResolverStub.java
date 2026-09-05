package ru.latyshev.workflow.activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.scheme.ExecutionContext;

@Slf4j
@Component
public class PlaceHolderResolverStub implements PlaceHolderResolver {

    @Override
    public JsonNode resolve(ExecutionContext context, String placeHolder) {
        log.info("Stub placeholder resolver resolved placeHolder={}", placeHolder);
        ObjectNode stubData = JsonNodeFactory.instance.objectNode();
        stubData.put("stubData", placeHolder);
        return stubData;
    }
}
