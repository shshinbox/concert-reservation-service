package me.songha.concert.reservation.preoccupy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PreoccupyController.class)
class PreoccupyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PreoccupySeatService preoccupySeatService;

    private PreoccupySeatRequest request;
    private PreoccupySeatsResponse response;

    @BeforeEach
    void setUp() {
        request = new PreoccupySeatRequest();
        request.setConcertId(1L);
        request.setSeatNumbers(List.of("A1", "A2"));

        response = new PreoccupySeatsResponse(1L, 1L, List.of("A1", "A2"), "msg");
    }

    @Test
    void preoccupySeats() throws Exception {
        Mockito.when(preoccupySeatService.preoccupySeats(eq(1L), eq(1L), any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/reservation/preoccupy")
                        .header("Authorization", "Bearer fake-jwt-token") // Mock JWT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.concertId").value(1L))
                .andExpect(jsonPath("$.seatNumbers").value(List.of("A1", "A2")))
                .andExpect(jsonPath("$.msg").value("msg"));

//        Mockito.verify(preoccupySeatService, Mockito.times(1))
//                .preoccupySeats(eq(1L), eq(123L), eq(List.of("A1", "A2"), eq(1L)));
    }
}