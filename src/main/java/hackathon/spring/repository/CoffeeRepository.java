package hackathon.spring.repository;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.web.dto.CoffeeDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.sql.rowset.BaseRowSet;
import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByCaffeineBetweenOrderByCaffeineAsc(int minCaffeine, int maxCaffeine);
//    List<Coffee> findAllByName(String name);
//    List<Coffee> findAllByBrand(Brand brand);
    List<Coffee> findTop5ByOrderByDrinkCountDesc();

    @Query(value = "SELECT * FROM coffee c WHERE "
            + "c.name LIKE %:keyword% OR "
            + "CAST(c.brand AS CHAR) LIKE %:keyword% OR " // ✅ string → CHAR 변경
            + "(REPLACE(CONCAT(CAST(c.brand AS CHAR), ' ', c.name), ' ', '') LIKE %:keyword%) "
            + "OR (CONCAT(c.brand, ' ', c.name) REGEXP :regexPattern)",
            nativeQuery = true) // ✅ MySQL 네이티브 쿼리로 변경
    Page<Coffee> findByBrandOrNameContaining(
            @Param("keyword") String keyword,
            @Param("regexPattern") String regexPattern,
            Pageable pageable);


    // 커피의 drinkCount를 1 증가시키는 메서드 (커피 id를 사용)
    @Modifying
    @Transactional
    @Query("UPDATE Coffee c SET c.drinkCount = c.drinkCount + 1 WHERE c.id = :id")
    void add1DrinkCount(@Param("id") Long id);

    Coffee findCoffeeById(Long coffeeId);
}
