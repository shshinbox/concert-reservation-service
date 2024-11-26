package me.songha.concert.reservationseat;

import com.github.fppt.jedismock.RedisServer;
import me.songha.concert.reservation.seat.ReservationSeatPreoccupyService;
import me.songha.concert.reservation.seat.ReservationSeatRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
class ReservationSeatPreoccupyServiceTest {
    private static RedisServer redisServer;
    private StringRedisTemplate redisTemplate;
    private ReservationSeatRepository reservationSeatRepository;
    private ReservationSeatPreoccupyService reservationSeatPreoccupyService;

    @BeforeAll
    static void startMockRedis() throws IOException {
        redisServer = RedisServer.newRedisServer(); // Mock Redis 서버 시작
        redisServer.start();
    }

    @AfterAll
    static void stopMockRedis() {
        redisServer.stop(); // Mock Redis 서버 종료
    }

    @BeforeEach
    void setUp() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(redisServer.getBindPort());
        jedisConnectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        reservationSeatRepository = Mockito.mock(ReservationSeatRepository.class);
//        reservationSeatPreoccupyService = new ReservationSeatPreoccupyService(redisTemplate, reservationSeatRepository);
    }

    @DisplayName("좌석을 선점한다.")
    @Test
    void preoccupySeats() {
        boolean result = reservationSeatPreoccupyService.preoccupySeats(1L, "A1", 1L);
        assertTrue(result);

        String value = redisTemplate.opsForValue().get("concert:1:seat:A1");
        assertEquals("1", value);
    }

    @DisplayName("선점된 좌석을 예매 시도한다.")
    @Test
    void preoccupySeats_AlreadyPreoccupySeats() {
        redisTemplate.opsForValue().set("concert:1:seat:A2", "1");

        boolean result = reservationSeatPreoccupyService.preoccupySeats(1L, "A2", 2L);
        assertFalse(result);
    }

    @DisplayName("팔린 좌석을 확인한다.")
    @Test
    void getSoldSeatsByConcert() {
        Mockito.when(reservationSeatRepository.findSeatNumbersByConcertId(1L))
                .thenReturn(Collections.singletonList("A3"));

        List<String> soldSeats = reservationSeatPreoccupyService.getSoldSeatsByConcert(1L);
        assertEquals(1, soldSeats.size());
        assertTrue(soldSeats.contains("A3"));
    }

    @DisplayName("선점한 좌석을 확인한다.")
    @Test
    void getReservedSeatsByConcert() {
        redisTemplate.opsForValue().set("concert:1:seat:A4", "1");
        redisTemplate.opsForValue().set("concert:1:seat:A5", "2");

        Set<String> reservedSeats = reservationSeatPreoccupyService.getPreoccupiedSeatsByConcert(1L);
        assertEquals(2, reservedSeats.size());
        assertTrue(reservedSeats.contains("concert:1:seat:A4"));
        assertTrue(reservedSeats.contains("concert:1:seat:A5"));
    }

    @DisplayName("예약 가능한 좌석을 확인한다.")
    @Test
    void isSeatAvailable() {
        Mockito.when(reservationSeatRepository.findIdsByConcertId(1L))
                .thenReturn(Collections.singletonList(1L));
        redisTemplate.opsForValue().set("concert:1:seat:A6", "1");

        assertFalse(reservationSeatPreoccupyService.isSeatAvailable(1L, "A6"));
        assertTrue(reservationSeatPreoccupyService.isSeatAvailable(1L, "A7"));
        assertTrue(reservationSeatPreoccupyService.isSeatAvailable(1L, "A8"));
    }
}
