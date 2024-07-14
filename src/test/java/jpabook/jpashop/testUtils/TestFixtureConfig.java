package jpabook.jpashop.testUtils;


import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.CategoryRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestFixtureConfig {

    @Bean
    public TestDataFixture testDataFixture(
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ){
        return new TestDataFixture(
                userRepository,
                accountRepository,
                categoryRepository,
                productRepository,
                orderRepository
        );
    }
}
