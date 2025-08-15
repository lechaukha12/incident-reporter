package com.nganhang.sentinel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.model.IncidentUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IncidentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateIncidentAndAddUpdate() throws Exception {
        // 1. Tạo incident mới
        IncidentCreateDTO createDTO = new IncidentCreateDTO();
        createDTO.setTitle("Test Incident");
        createDTO.setDescription("Test Description");
        createDTO.setSeverityLevel(Incident.SeverityLevel.HIGH);
        createDTO.setAffectedService("Test Service");
        createDTO.setReportedBy("Test User");

        MvcResult result = mockMvc.perform(post("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Test Incident")))
                .andExpect(jsonPath("$.status", is("INVESTIGATING")))
                .andExpect(jsonPath("$.timeline", hasSize(1)))
                .andReturn();

        // Lấy ID của incident vừa tạo
        String response = result.getResponse().getContentAsString();
        Integer incidentId = objectMapper.readTree(response).get("id").asInt();

        // 2. Thêm cập nhật vào timeline
        TimelineUpdateDTO updateDTO = new TimelineUpdateDTO();
        updateDTO.setMessage("Test Update Message");
        updateDTO.setUpdateType(IncidentUpdate.UpdateType.INVESTIGATION);
        updateDTO.setCreatedBy("Test Updater");

        mockMvc.perform(post("/api/incidents/{id}/updates", incidentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeline", hasSize(2)))
                .andExpect(jsonPath("$.timeline[0].message", is("Test Update Message")));

        // 3. Cập nhật trạng thái issue
        IncidentUpdateDTO incidentUpdateDTO = new IncidentUpdateDTO();
        incidentUpdateDTO.setStatus(Incident.IncidentStatus.IDENTIFIED);
        incidentUpdateDTO.setSeverityLevel(Incident.SeverityLevel.CRITICAL);

        mockMvc.perform(put("/api/incidents/{id}", incidentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incidentUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IDENTIFIED")))
                .andExpect(jsonPath("$.severityLevel", is("CRITICAL")));

        // 4. Lấy danh sách tất cả issue
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

        // 5. Lấy thông tin issue theo ID
        mockMvc.perform(get("/api/incidents/{id}", incidentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(incidentId)))
                .andExpect(jsonPath("$.title", is("Test Incident")))
                .andExpect(jsonPath("$.status", is("IDENTIFIED")))
                .andExpect(jsonPath("$.timeline", hasSize(2)));

        // 6. Đánh dấu issue đã giải quyết
        IncidentUpdateDTO resolveDTO = new IncidentUpdateDTO();
        resolveDTO.setStatus(Incident.IncidentStatus.RESOLVED);
        resolveDTO.setResolved(true);

        mockMvc.perform(put("/api/incidents/{id}", incidentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resolveDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVED")))
                .andExpect(jsonPath("$.resolved", is(true)))
                .andExpect(jsonPath("$.resolvedAt", notNullValue()));

        // 7. Lấy thống kê
        mockMvc.perform(get("/api/incidents/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncidents", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.resolvedIncidents", greaterThanOrEqualTo(1)));
    }
}
