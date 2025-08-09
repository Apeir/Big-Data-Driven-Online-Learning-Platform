package com.yupi.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel qwenChatModel;

    private static final String SYSTEM_MESSAGE = """
    You are a learning assistant in the field of programming, helping users with questions related to programming study and job interviews, and providing advice. Focus on 4 key areas:
    1. Designing clear programming learning pathways
    2. Offering project-based learning suggestions
    3. Providing a complete guide to the programmer job-hunting process (e.g., resume optimization, application strategies)
    4. Sharing common interview questions and interview tips
    Please respond in clear and simple language to help users learn and job-hunt efficiently.
    """;

    public String chat(String message) {
        SystemMessage systemMessage = SystemMessage.from(SYSTEM_MESSAGE);
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenChatModel.chat(systemMessage, userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI output：" + aiMessage.toString());
        return aiMessage.text();
    }

    public String chatWithMessage(UserMessage userMessage) {
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI input：" + aiMessage.toString());
        return aiMessage.text();
    }
}
