package hackathon.spring.service;

import hackathon.spring.domain.Coffee;
import hackathon.spring.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
  
    public Coffee addCoffee(Coffee coffee) {
        // Coffee 객체 저장
        return coffeeRepository.save(coffee);
    }

    public List<Coffee> recommendByCaffeineLimit(LocalDateTime userTimeInput) {
        long t = ChronoUnit.MINUTES.between(LocalDateTime.now(), userTimeInput);

        int minCaffeine = 0;
        int maxCaffeine = 0;

        if (t >= 480) {
            maxCaffeine = Integer.MAX_VALUE;
        } else if (t >= 390) {
            maxCaffeine = 150;
        } else if (t >= 300) {
            maxCaffeine = 120;
        } else if (t >= 240) {
            maxCaffeine = 100;
        } else {
            maxCaffeine = 0;
        }

        List<Coffee> coffeeList = coffeeRepository.findByCaffeineBetweenOrderByCaffeineAsc(minCaffeine, maxCaffeine);
        Collections.shuffle(coffeeList);  // 리스트를 무작위로 섞기
        return coffeeList.stream()
                .limit(5)         // 상위 5개만 반환
                .collect(Collectors.toList());
    }

}
