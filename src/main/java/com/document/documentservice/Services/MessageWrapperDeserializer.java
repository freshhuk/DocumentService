package com.document.documentservice.Services;

import com.document.documentservice.Domain.Models.DocumentDTO;
import com.document.documentservice.Domain.Models.LoginModel;
import com.document.documentservice.Domain.Models.MessageWrapper;
import com.document.documentservice.Domain.Models.RegisterModel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MessageWrapperDeserializer extends JsonDeserializer<MessageWrapper<?>> {

    @Override
    public MessageWrapper<?> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String action = node.get("action").asText();

        Object payload;

        if (node.has("payload")) {
            JsonNode payloadNode = node.get("payload");

            if (payloadNode.isTextual()) {
                payload = payloadNode.asText();  // String
            }
            else if (payloadNode.has("fileName") && payloadNode.has("fileType")) {
                payload = jp.getCodec().treeToValue(payloadNode, DocumentDTO.class);  //  DocumentDTO
            } else if (payloadNode.has("login") && payloadNode.has("password")) {//  login model
                payload = jp.getCodec().treeToValue(payloadNode, LoginModel.class);
            } else if (payloadNode.has("loginRegister") && payloadNode.has("password") && payloadNode.has("confirmPassword")) {
                payload = jp.getCodec().treeToValue(payloadNode, RegisterModel.class);  // RegisterModel
            } else {
                payload = jp.getCodec().treeToValue(payloadNode, Object.class); // default
            }
        } else {
            payload = null;
        }

        return new MessageWrapper<>(action, payload);
    }
}

