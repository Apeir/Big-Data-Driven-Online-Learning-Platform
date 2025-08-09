package com.yupi.aicodehelper.ai;

import dev.langchain4j.agent.ReActAgent;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.goodstudy.ai.tools.InformationSearchTool;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private InformationSearchTool informationSearchTool;

    private ReActAgent agent;

    @jakarta.annotation.PostConstruct
    public void initAgent() {
        agent = ReActAgent.builder()
                .chatModel(qwenChatModel)
                .tools(informationSearchTool)
                .build();
    }

    public String chat(String message) {
        String result = agent.execute(message).content();
        log.info("AI output：" + result);
        return result;
    }
}