package hackathon.spring.service;

import hackathon.spring.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
}
