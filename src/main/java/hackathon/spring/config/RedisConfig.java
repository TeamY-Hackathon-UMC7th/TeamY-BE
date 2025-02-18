package hackathon.spring.config;

import hackathon.spring.web.dto.CoffeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@EnableRedisRepositories(redisTemplateRef = "searchRedisTemplate")
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        lettuceConnectionFactory.setDatabase(1); // 1번 DB에서 최근 추천받은 메뉴 저장
        return lettuceConnectionFactory;
    }

//    @Bean
//    public RedisTemplate<String, String> searchRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, String> searchRedisTemplate = new RedisTemplate<>();
//        searchRedisTemplate.setConnectionFactory(redisConnectionFactory);
//        searchRedisTemplate.setKeySerializer(new StringRedisSerializer()); // Key - 문자열
//        searchRedisTemplate.setValueSerializer(new StringRedisSerializer()); // Value - 문자열
//
//        return searchRedisTemplate;
//    }

    @Bean
    public RedisTemplate<String, List<CoffeeDto.CoffeePreviewDTO>> searchRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<CoffeeDto.CoffeePreviewDTO>> searchRedisTemplate = new RedisTemplate<>();
        searchRedisTemplate.setConnectionFactory(redisConnectionFactory);
        searchRedisTemplate.setKeySerializer(new StringRedisSerializer()); // Key - 문자열
        searchRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class)); // Value - JSON 직렬화

        return searchRedisTemplate;
    }

}
