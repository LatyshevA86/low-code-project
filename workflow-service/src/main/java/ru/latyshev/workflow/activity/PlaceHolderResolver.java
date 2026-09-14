package ru.latyshev.workflow.activity;

import com.fasterxml.jackson.databind.JsonNode;
import ru.latyshev.workflow.scheme.ExecutionContext;

public interface PlaceHolderResolver {

    JsonNode resolve(ExecutionContext context, String placeHolder);
}
