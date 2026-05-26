package com.yupi.ai_code_helper.ai.model;


import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("langchain4j.community.dashscope.chat-model")
@Data
public class QwenChatModelConfig {
    private String apiKey;
    private String modelName;

    @Resource
    private ChatModelListener chatModelListener;
    @Bean
    public ChatModel MyQwenChatModel() {

        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of( chatModelListener))
                .build();
    }

}
