package hackathon.spring.service;

import hackathon.spring.domain.Coffee;
import hackathon.spring.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    public Coffee addCoffee(Coffee coffee) {
        // Coffee 객체 저장
        return coffeeRepository.save(coffee);
    }




}
