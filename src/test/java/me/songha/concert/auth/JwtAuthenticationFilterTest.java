package me.songha.concert.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.songha.concert.reservation.general.ReservationDto;
import me.songha.concert.reservation.general.ReservationNotFoundException;
import me.songha.concert.reservation.general.ReservationRepositoryService;
import me.songha.concert.venue.VenueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private SecretKey key;
    private String validToken;
    private String validTokenAdminRole;
    private String expiredToken;

    @MockBean
    private ReservationRepositoryService reservationRepositoryService;

    @BeforeEach
    void setUp() {
        String SECRET_KEY = "my-fixed-secret-key-my-fixed-secret-key";
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        validToken = Jwts.builder()
                .setSubject("user123")
                .claim("role", "USER")
                .claim("userId", 1L)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000))
                .signWith(key)
                .compact();

        expiredToken = Jwts.builder()
                .setSubject("user123")
                .claim("role", "USER")
                .claim("userId", 1L)
                .setExpiration(new Date(System.currentTimeMillis() - 60 * 1000))
                .signWith(key)
                .compact();

        validTokenAdminRole = Jwts.builder()
                .setSubject("user123")
                .claim("role", "ADMIN")
                .claim("userId", 1L)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000))
                .signWith(key)
                .compact();

        ReservationDto reservationDto = ReservationDto.builder().id(1L).userId(1L).totalAmount(100).build();

        Mockito.when(reservationRepositoryService.getReservation(1L))
                .thenReturn(reservationDto);

        Mockito.when(reservationRepositoryService.getReservation(99L))
                .thenThrow(new ReservationNotFoundException("[Error] Reservation not found."));
    }

    @Test
    void 올바른_토큰_인증_성공() throws Exception {
        mockMvc.perform(get("/reservation/my")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 토큰_없음_401_응답() throws Exception {
        mockMvc.perform(get("/reservation/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 만료된_토큰_401_응답() throws Exception {
        mockMvc.perform(get("/reservation/my")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된_토큰_401_응답() throws Exception {
        String invalidToken = "invalid.token.value";

        mockMvc.perform(get("/reservation/my")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 권한없음_403_응답() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(VenueDto.builder().name("venue").build());

        mockMvc.perform(post("/api/venue")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void 권한보유_201_응답() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(VenueDto.builder().name("venue").build());

        mockMvc.perform(post("/api/venue")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validTokenAdminRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void generateJWT() {
        String jwt = Jwts.builder()
                .claim("userId", 567L)
                .setSubject("userId123")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();

        System.out.println(jwt);
    }
}
