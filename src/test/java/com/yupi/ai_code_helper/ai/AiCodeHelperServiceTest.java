package com.yupi.ai_code_helper.ai;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好");
        System.out.println(result);
    }
    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好，我是鱼皮");
        System.out.println(result);
        String result2 = aiCodeHelperService.chat("我是谁?");
        System.out.println(result2);
    }

    @Test
    void chatForReport() {
        String userMessage="你好，我是鱼皮，请问我制定学习报表";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }
    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学java，常见的面试题有哪些？");
        System.out.println(result.content());
        System.out.println(result.sources());
    }
    @Test
    void chatWithTool() {
        String result = aiCodeHelperService.chat("有哪些常见的计算机网络面试题？");
        System.out.println(result);
    }
    @Test
    void chatWithMcp() {
        String result = aiCodeHelperService.chat("什么是程序员鱼皮的编程导航？");
        System.out.println(result);
    }
    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat("kil");
        System.out.println(result);
    }

}