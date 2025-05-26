package com.xuecheng.content.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeachplanController.class)
@AutoConfigureMockMvc(addFilters = false) // 关闭 Security 过滤器链
class TeachplanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TeachplanService teachplanService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testGetTreeNodes() throws Exception {
        TeachplanDto dto = new TeachplanDto();
        Mockito.when(teachplanService.findTeachplanTree(1L)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/teachplan/1/tree-nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSaveTeachplan() throws Exception {
        SaveTeachplanDto dto = new SaveTeachplanDto();
        mockMvc.perform(post("/teachplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(teachplanService).saveTeachplan(any(SaveTeachplanDto.class));
    }

    @Test
    void testAssociationMedia() throws Exception {
        BindTeachplanMediaDto dto = new BindTeachplanMediaDto();
        mockMvc.perform(post("/teachplan/association/media")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(teachplanService).associationMedia(any(BindTeachplanMediaDto.class));
    }
}
