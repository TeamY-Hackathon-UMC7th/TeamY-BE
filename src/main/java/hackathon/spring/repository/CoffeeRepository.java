package hackathon.spring.repository;

import hackathon.spring.domain.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByCaffeineBetweenOrderByCaffeineAsc(int minCaffeine, int maxCaffeine);
}
