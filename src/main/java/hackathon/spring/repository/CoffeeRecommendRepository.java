//package hackathon.spring.repository;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import hackathon.spring.web.dto.CoffeeDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//public class CoffeeRecommendRepository {
//    private final RedisTemplate<String, String> searchRedisTemplate;
//    private static final String RECOMMEND_COFFEE_PREFIX = "recommend:";
//    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기
//
//    /**
//     * 사용자별 최근 추천 커피 저장 (최대 5개 유지)
//     */
//    public void saveRecentCoffee(String userEmail, CoffeeDto.CoffeePreviewDTO coffee) {
//        String redisKey = RECOMMEND_COFFEE_PREFIX + userEmail;
//        ListOperations<String, String> listOps = searchRedisTemplate.opsForList();
//
//        try {
//            String coffeeJson = objectMapper.writeValueAsString(coffee); // CoffeePreviewDTO를 JSON으로 변환
//            listOps.leftPush(redisKey, coffeeJson); // 최신 커피를 리스트에 추가
//            listOps.trim(redisKey, 0, 4); // 최근 5개만 유지
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Redis 저장 중 오류 발생", e);
//        }
//    }
//
//    /**
//     * 사용자별 최근 추천 커피 5개 조회
//     */
//    public List<CoffeeDto.CoffeePreviewDTO> getRecentRecommendedCoffees(String userEmail) {
//        String redisKey = RECOMMEND_COFFEE_PREFIX + userEmail;
//        ListOperations<String, String> listOps = searchRedisTemplate.opsForList();
//        List<String> jsonList = listOps.range(redisKey, 0, 4); // 최근 5개 조회
//
//        return jsonList.stream().map(json -> {
//            try {
//                return objectMapper.readValue(json, CoffeeDto.CoffeePreviewDTO.class);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("Redis 조회 중 오류 발생", e);
//            }
//        }).collect(Collectors.toList());
//    }
//
//
//}
