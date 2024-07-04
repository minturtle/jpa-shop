### JPA SHOP(쇼핑몰 프로젝트)

- 쇼핑몰 프로젝트를 스프링 웹으로 제작


- 사용된 기술 
  - Spring Boot
  - Spring Data Jpa
  - MySql
  - H2 Database
  - Swagger
  - Spring Security

### 문서 바로가기

- [요구사항 명세서 바로가기](https://github.com/minturtle/jpa-shop/blob/master/docs/SYSTEM_REQUIREMENTS.md)
- [기능 약정 바로가기](https://github.com/minturtle/jpa-shop/blob/master/docs/OPERATION_CONTRACT.md)

### Domain Model
![Domain Model](docs/domain_model.png)


### Class Diagram
![Class Diagram](docs/class_diagram.png)


### 기술적 요구사항

- 계좌 관련 요구사항
  - 입금에 대해서는 동시성 요청을 허가하나, 동시에 들어온 요청에 대해 순차적으로 입금을 수행한다.
  - 출금에 대해서는 동시성 요청을 허가하지 않는다. 즉 동시에 요청이 들어온 경우, 처음 출금 요청만 성공하고 나머지는 실패해야 한다.
  - 입금과 출금이 동시에 요청된 경우, 입금은 수행되나 출금은 실패해야 한다.
  - 계좌의 잔액은 Long Type을 사용한다.
- 주문 관련 요구사항
  - 주문 금액, 물품 금액에 대해선 모두 Integer 타입을 사용한다.