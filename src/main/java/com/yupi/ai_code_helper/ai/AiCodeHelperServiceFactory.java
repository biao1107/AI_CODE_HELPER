package com.yupi.ai_code_helper.ai;

import com.yupi.ai_code_helper.ai.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {
    @Resource
    private ChatModel  MyQwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;
    @Resource
    private McpToolProvider   mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;
    @Bean
    public AiCodeHelperService aiCodeHelperService(){

        //会话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        //构建Ai Service
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(MyQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)//流式输出
                .chatMemoryProvider(memoryId ->MessageWindowChatMemory.withMaxMessages(10))//每个会话独立存储
                /*.chatMemory(chatMemory)//会话记忆*/
                .contentRetriever(contentRetriever)//Rag 检索增强生成
                .tools(new InterviewQuestionTool())//tool工具调用
                .toolProvider(mcpToolProvider)//mcp工具调用
                .build();
        return aiCodeHelperService;
    }

}
