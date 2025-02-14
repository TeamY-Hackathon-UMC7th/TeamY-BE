package hackathon.spring.repository;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.sql.rowset.BaseRowSet;
import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByCaffeineBetweenOrderByCaffeineAsc(int minCaffeine, int maxCaffeine);
//    List<Coffee> findAllByName(String name);
//    List<Coffee> findAllByBrand(Brand brand);
    @Query("SELECT c FROM Coffee c WHERE " +
            "c.name LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(c.brand AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Coffee> findByBrandOrNameContaining(@Param("keyword") String keyword);


}
