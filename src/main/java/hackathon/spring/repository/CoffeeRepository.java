package hackathon.spring.repository;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.sql.rowset.BaseRowSet;
import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByCaffeineBetweenOrderByCaffeineAsc(int minCaffeine, int maxCaffeine);
//    List<Coffee> findAllByName(String name);
//    List<Coffee> findAllByBrand(Brand brand);
    List<Coffee> findTop5ByOrderByDrinkCountDesc();
    @Query("SELECT c FROM Coffee c WHERE "
            + "c.name LIKE %:keyword% OR "
            + "CAST(c.brand AS string) LIKE %:keyword% OR "
            + "REPLACE(CONCAT(CAST(c.brand AS string), ' ', c.name), ' ', '') "
            + "LIKE REPLACE(CONCAT('%', :keyword, '%'), ' ', '')")
    Page<Coffee> findByBrandOrNameContaining(@Param("keyword") String keyword, Pageable pageable);
}
