package com.ecar.ecarservice;

import com.ecar.ecarservice.controller.MaintenanceController;
import com.ecar.ecarservice.dto.AdditionalCostRequest;
import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.dto.UsedPartDto;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import com.ecar.ecarservice.service.MaintenanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MaintenanceController.class)
@Import(MaintenanceControllerTest.TestSecurityConfig.class)
class MaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaintenanceService maintenanceService;

    private OidcLoginRequestPostProcessor oidcUser() {
        return SecurityMockMvcRequestPostProcessors.oidcLogin().idToken(token -> token.claim("sub", "user-1").claim("email", "user@test.com"));
    }

    @Test
    @DisplayName("Should return paged maintenance history for authenticated user")
    void getMaintenanceHistory_returnsPage() throws Exception {
        Page<MaintenanceHistoryDTO> page = new PageImpl<>(List.of(Mockito.mock(MaintenanceHistoryDTO.class)));
        when(maintenanceService.getMaintenanceHistory(any(), any(MaintenanceHistorySearchRequest.class)))
                .thenReturn(page);

        MaintenanceHistorySearchRequest req = new MaintenanceHistorySearchRequest();
        mockMvc.perform(post("/api/maintenance/history")
                        .with(oidcUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(maintenanceService).getMaintenanceHistory(any(), any(MaintenanceHistorySearchRequest.class));
    }

    @Test
    @DisplayName("Should create schedule and return 201 with ticketId")
    void createSchedule_returnsCreated() throws Exception {
        MaintenanceScheduleRequest req = new MaintenanceScheduleRequest();
        MaintenanceHistory ticket = new MaintenanceHistory();
        ticket.setId(123L);
        when(maintenanceService.createSchedule(any(MaintenanceScheduleRequest.class), any())).thenReturn(ticket);

        mockMvc.perform(post("/api/maintenance/create")
                        .with(oidcUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value(123));

        verify(maintenanceService).createSchedule(any(MaintenanceScheduleRequest.class), any());
    }

    @Test
    @DisplayName("Should handle technician completion with not found and bad request scenarios")
    void completeTaskByTechnician_handlesErrors() throws Exception {
        // Success
        when(maintenanceService.completeServiceByTechnician(eq(1L), any())).thenReturn(Mockito.mock(MaintenanceHistoryDTO.class));

        mockMvc.perform(post("/api/maintenance/1/technician-complete").with(oidcUser()))
                .andExpect(status().isOk());

        // Not found
        when(maintenanceService.completeServiceByTechnician(eq(2L), any())).thenThrow(new EntityNotFoundException());
        mockMvc.perform(post("/api/maintenance/2/technician-complete").with(oidcUser()))
                .andExpect(status().isNotFound());

        // Bad request
        when(maintenanceService.completeServiceByTechnician(eq(3L), any())).thenThrow(new AccessDeniedException("denied"));
        mockMvc.perform(post("/api/maintenance/3/technician-complete").with(oidcUser()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update and get used parts for a task")
    void usedParts_updateAndGet() throws Exception {
        // Update
        var update = Map.of("usedParts", List.of(new UsedPartDto(10L, 2)));
        doNothing().when(maintenanceService).updateUsedParts(eq(7L), any());

        mockMvc.perform(put("/api/maintenance/tasks/7/used-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(maintenanceService).updateUsedParts(eq(7L), any());

        // Get
        when(maintenanceService.getUsedParts(7L)).thenReturn(List.of(new UsedPartDto(10L, 2)));
        mockMvc.perform(get("/api/maintenance/tasks/7/used-parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].partId").value(10))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @DisplayName("Should add additional cost and allow customer approval")
    void additionalCost_addAndApprove() throws Exception {
        // Staff adds cost
        AdditionalCostRequest addReq = new AdditionalCostRequest();
        addReq.setAmount(new BigDecimal("50.00"));
        addReq.setReason("Extra work");
        doNothing().when(maintenanceService).addOrUpdateAdditionalCost(eq(9L), eq(new BigDecimal("50.00")), eq("Extra work"));

        mockMvc.perform(put("/api/maintenance/tasks/9/additional-cost")
                        .with(SecurityMockMvcRequestPostProcessors.user("staff").roles("STAFF"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk());

        // Customer approves
        doNothing().when(maintenanceService).approveAdditionalCost(eq(9L), any());
        mockMvc.perform(post("/api/maintenance/tasks/9/approve-cost")
                        .with(SecurityMockMvcRequestPostProcessors.user("customer").roles("CUSTOMER")))
                .andExpect(status().isOk());

        verify(maintenanceService).addOrUpdateAdditionalCost(eq(9L), eq(new BigDecimal("50.00")), eq("Extra work"));
        verify(maintenanceService).approveAdditionalCost(eq(9L), any());
    }

    @Test
    @DisplayName("Should fetch milestones, service groups, and tickets with proper roles")
    void miscEndpoints_work() throws Exception {
        when(maintenanceService.getMilestone(5L)).thenReturn(List.of(Mockito.mock(MilestoneResponse.class)));
        when(maintenanceService.getMaintenanceServiceGroup(5L, 6L)).thenReturn(List.of(Mockito.mock(ServiceGroup.class)));
        when(maintenanceService.getServiceGroup(11L)).thenReturn(List.of(Mockito.mock(ServiceGroup.class)));
        when(maintenanceService.getTickets(any())).thenReturn(List.of(Mockito.mock(MaintenanceTicketResponse.class)));

        mockMvc.perform(get("/api/maintenance/milestone/5"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/maintenance/service-group/5/6"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/maintenance/service-group/11"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/maintenance/all").with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should assign task and cancel/reopen maintenance with role checks")
    void assignAndStateTransitions() throws Exception {
        doNothing().when(maintenanceService).assignTask(any(ServiceCreateRequest.class));
        doNothing().when(maintenanceService).cancelMaintenance(100L);
        doNothing().when(maintenanceService).reopenMaintenance(100L);

        mockMvc.perform(post("/api/maintenance/assign-task")
                        .with(SecurityMockMvcRequestPostProcessors.user("staff").roles("STAFF"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ServiceCreateRequest())))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/maintenance/100/cancel").with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/maintenance/100/reopen").with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        verify(maintenanceService).assignTask(any(ServiceCreateRequest.class));
        verify(maintenanceService).cancelMaintenance(100L);
        verify(maintenanceService).reopenMaintenance(100L);
    }

    // ================= NEW TESTS FOR RECENT ENDPOINTS/BEHAVIORS =================

    @Test
    @DisplayName("Should return tickets for ADMIN and STAFF roles")
    void getTickets_roleAccess() throws Exception {
        when(maintenanceService.getTickets(any())).thenReturn(List.of(Mockito.mock(MaintenanceTicketResponse.class)));

        // ADMIN can access
        mockMvc.perform(get("/api/maintenance/all")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        // STAFF can access
        mockMvc.perform(get("/api/maintenance/all")
                        .with(SecurityMockMvcRequestPostProcessors.user("staff").roles("STAFF")))
                .andExpect(status().isOk());

        verify(maintenanceService, times(2)).getTickets(any());
    }

    @Test
    @DisplayName("Should pass OIDC principal with email claim to getTickets")
    void getTickets_passesPrincipal() throws Exception {
        when(maintenanceService.getTickets(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/maintenance/all").with(oidcUser()))
                .andExpect(status().isOk());

        ArgumentCaptor<OidcUser> captor = ArgumentCaptor.forClass(OidcUser.class);
        verify(maintenanceService).getTickets(captor.capture());
        assertEquals("user@test.com", captor.getValue().getAttribute("email"));
    }

    @Test
    @DisplayName("Should map createService to /service-create and return 200")
    void createService_endpointMapping() throws Exception {
        doNothing().when(maintenanceService).createService(any(ServiceCreateRequest.class), any());

        mockMvc.perform(post("/api/maintenance/service-create")
                        .with(SecurityMockMvcRequestPostProcessors.user("staff").roles("STAFF"))
                        .with(oidcUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ServiceCreateRequest())))
                .andExpect(status().isOk());

        verify(maintenanceService).createService(any(ServiceCreateRequest.class), any());
    }

    @Test
    @DisplayName("Should call technician completion via new PUT mapping and return 200")
    void technicianComplete_putMapping() throws Exception {
        doNothing().when(maintenanceService).completeServiceByTechnician(eq(55L), any());

        mockMvc.perform(put("/api/maintenance/technician/tasks/55/complete")
                        .with(SecurityMockMvcRequestPostProcessors.user("tech").roles("TECHNICIAN"))
                        .with(oidcUser()))
                .andExpect(status().isOk());

        verify(maintenanceService).completeServiceByTechnician(eq(55L), any());
    }

    @Test
    @DisplayName("Should return technician's own tasks from /technician/my-tasks")
    void getMyTasks_returnsForTechnician() throws Exception {
        when(maintenanceService.getTicketsForTechnician(any())).thenReturn(List.of(Mockito.mock(MaintenanceTicketResponse.class)));

        mockMvc.perform(get("/api/maintenance/technician/my-tasks")
                        .with(SecurityMockMvcRequestPostProcessors.user("tech").roles("TECHNICIAN"))
                        .with(oidcUser()))
                .andExpect(status().isOk());

        verify(maintenanceService).getTicketsForTechnician(any());
    }

    @Configuration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}
