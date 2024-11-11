## Querydsl
>
**Querydsl**이란 정적 타입을 이용하여 SQL과 같은 쿼리를 생성할 수 있도록 지원하는 ***<u>라이브러리를 의미한다.</u>*** 
즉 SQL 형식의 쿼리를 Type-Safe하게 생성할 수 있도록 DSL을 제공하는 라이브러리인 것이다.

- 여기서 "dsl"이라는 용어는 특정 도메인에서 발생하는 문제를 효과적으로 해결하기 위해 설계된 언어이다.
- `@Query`를 사용해서 직접 JPQL의 쿼리를 보통 작성할 것이다. 
하지만 직접 문자열을 입력하기 때문에 컴파일 시점에서 에러를 잡아내지 못한다.
- **Querydsl의 주요 장점 중 하나로서 컴파일 시점에서 쿼리 오류를 잡아낼 수 있다.**
- IDE가 제공하는 코드 자동 완성 기능을 사용할 수 있다.
- **주의점** : Spring Boot 2에서 3로 올라오면서 javax가 jakarta로 변경되었다.

***

## Querydsl 사용방법

### 1. Querydsl - JPA 의존성 추가

```java
implementaion 'com.querydsl:querydsl-jpa:5.0.0:jakarata'
```

해당 코드를 추가하는데, querydsl-jpa 버전 명시 뒤에 :jakarta 추가 꼭 하기!!

### 2. QClass 생성을 위한 annotaionProcessor 추가

```java
    annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"
```
- QClass란 엔티티 클래스 속성과 구조를 설명해주는 메타데이터를 의미한다. Type-Safe하게 쿼리 조건 설정이 가능하다.

### 3. build.gradle내 querydsl 세팅 하기

```java
// querydsl 세팅 시작
def querydslDir = "$buildDir/generated/querydsl"

sourceSets {
    main.java.srcDir querydslDir
}
configurations {
    querydsl.extendsFrom compileClasspath
}
```

<details>
<summary>📜build.gradle 전체 코드 📜</summary>
<div>

```java
buildscript {
    ext {
        queryDslVersion = "5.0.0"
    }
}

plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.5'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.mysql:mysql-connector-j'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
    annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"
}

tasks.named('test') {
    useJUnitPlatform()
}
// querydsl 세팅 시작
def querydslDir = "$buildDir/generated/querydsl"

sourceSets {
    main.java.srcDir querydslDir
}
configurations {
    querydsl.extendsFrom compileClasspath
}
```

</div>
</details>

***

### 4. DB와 연동하는 방법

- application.properties에 다음 코드를 작성한다.

```java
spring.application.name=QueryDsl-tutorial
spring.datasource.url=jdbc:mysql://localhost:3306/querydsl
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username={나의 사용자 이름}
spring.datasource.password={나의 사용자가 접근할 비밀번호}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

logging.level.org.springframework.data.jpa.repository.query=DEBUG
logging.level.com.querydsl.jpa.impl.JPAQueryFactory=DEBUG
```
***
### 5. Entity 클래스 만들기
```java
package com.springboot.querydsltutorial.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Post> posts;
}

```
```java
package com.springboot.querydsltutorial.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
}

```

여기까지 잘 따라왔으면 build를 한번 실행해보자!
그렇게 되면 Q도메인 클래스가 생기는 걸 확인할 수 있다.
![](https://velog.velcdn.com/images/kkong_do/post/80a346fe-05d3-4c96-81ce-d21e0d314200/image.png)

나는 User 엔티티와 Post 엔티티를 만들었기 때문에 QUser 클래스, QPost 클래스가 만들어 졌다.

>**Q도메인 클래스**는 JPA 엔티티를 기반으로 생성되어 해당 엔티티의 필드와 관계를 정적으로 나타내는 java 클래스다. 

![](https://velog.velcdn.com/images/kkong_do/post/89f54728-1eeb-489c-8f03-522beed1ec65/image.png)
![](https://velog.velcdn.com/images/kkong_do/post/577f7098-953a-4dbc-955e-3a491e4485b0/image.png)


- Q도메인 클래스를 사용하면 쿼리를 작성할 때 문자열 기반의 필드명이나 테이블명을 사용하는 것보다 타입 안정성을 제공 받을 수 있다. 

***


### 6. JPAQueryFactory 빈을 등록하기
```java
package com.springboot.querydsltutorial.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDSLConfiguration {
    @PersistenceContext
    EntityManager entityManager;
    
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}

```

- QueryDSL 사용을 위한 Factory를 Bean으로 등록한다.
- JPAQueryFactory는 QueryDSL에서 제공하는 주요 클래스 중 하나이며, 해당 Config 파일을 만들어 JPAQueryFactory를 QueryDSL을 이용한 JPA 쿼리를 빌드하는 Factory 역할로서 사용할 수 있도록 Bean으로 등록해야 한다.

>
**@PersistenceContext**
JPA에서 사용하며 Entity Manager를 필드나 메서드 매개변수로 주입할 때 사용한다. 
Entity Manager는 JPA에서 영속성 컨텍스트를 관리하므로 트랜잭션 관리, 데이터베이스와 상호작용(CRUD) 등을 지원한다. 

***

### 7. 사용할 레포지터리 선언하기
- Repository는 총 3개를 만들어야 한다.
	1. **JpaRepository(Interface)**
	2.  직접 커스텀한 **CustomRepository(Interface)**
	3. 직접 커스텀한 CustomRepository(Interface)를 구현한 **CustomRepositoryImpl(Class)**구현체를 사용하게 된다.

1. **JpaRepository(Interface)**
```java
// UserRepository로서 JpaRepository랑 UserRepositoryCustom을 상속받는다. 
// (둘 다 인터페이스!!)
@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
}
```
2. **CustomRepository(Interface)**
```java
package com.springboot.querydsltutorial.repository;

import com.springboot.querydsltutorial.entity.User;

import java.util.List;

// 우리가 사용할 메서드를 선언해준다.
public interface UserRepositoryCustom {
    List<User> findUsersByName(String name);
    List<User> findUsersByNameAndEmail(String name, String email);
}
```
3. **CustomRepository(Class)**
```java
// 우리는 해당 CustomRepository를 이용해서 QueryDsl을 사용할 수 있게 되고, 동적 쿼리 또한 만들 수 있다.
package com.springboot.querydsltutorial.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.querydsltutorial.entity.QPost;
import com.springboot.querydsltutorial.entity.QUser;
import com.springboot.querydsltutorial.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

import static com.springboot.querydsltutorial.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findUsersByName(String name) {
        // RequestParam으로 받아온 이름을 조건식에 사용해서 해당 조건에 맞는 엔티티 가져오기
        QUser user = QUser.user;
        QPost post = QPost.post;
        return queryFactory.select(user).from(user)
                .leftJoin(user.posts, post).fetchJoin()
                .where(user.name.eq(name))
                .fetch();
    }

    @Override
    public List<User> findUsersByNameAndEmail(String name, String email) {
        QUser user = QUser.user;
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();
        // BooleanBuilder는 다중 조건 처리를 할 때, '()'가 들어가는 상황에서 좋고
        // BooleanExpression은 단순한 쿼리나 단일 조건일 때

        if (name != null && !name.isEmpty()) {
            builder.and(user.name.eq(name));
        }
        if( email != null && !email.isEmpty()) {
            builder.and(user.email.eq(email));
        }
        return queryFactory.selectFrom(user)
                .leftJoin(user.posts, post).fetchJoin()
                .where(builder)
                .fetch();
    }
}

```

- 동적 쿼리를 사용하기 위해서는 `BooleanBuilder`를 사용한다. 즉
BooleanBuilder를 이용해 조건절을 추가한 뒤 where절에 전달해서 동적으로 구현해 낼 수 있다.
-  JPAQueryFactory를 선언할 때 Entity의 타입을 명시해주지 않아도 되고 `selectFrom()`부터 시작한다. 만약 전체가 아니라 일부만 조회하고 싶다면 `.select().from()`과 같이 분리해서 작성하면 된다. 또한, select()에 `select(a,b,c)`와 같이 조회하고 싶은 일부만 뽑아낼 수 있다. 
- return 받으려면 `fetch()` 메서드를 사용해야 한다. 
한 건의 조회 경우 `fetchOne()`, 여러 건의 조회 결과 중 1건 반환은 `fetchFirst()`, 조회 결과의 개수는 `fetchCount()`, 조회 결과 리스트와 개수를 포함한 것은 `fetchResults()`를 사용하면 된다.

#### `BooleanBuilder`와 `BooleanExpression`의 차이점

- `BooleanBuilder`
	- **가변 객체**이며, 조건을 동적으로 추가하거나 제거할 수 있다는 점이 존재한다. 그러므로 상황에 따라 조건을 조합해야하는 동적 쿼리에 더 유용하게 사용할 수 있다.

- `BooleanExpression`
	- **불변 객체**이며, 한번 설정된 조건을 변경할 수 없다는 점이 존재한다. 그러므로 새로운 조건이 추가되면 새로운 `BooleanExpression`을 생성해야된다.
 
#### Expressions.numberTemplate
- Expressions.numberTemplate은 Querydsl에서 제공하는 메소드 중 하나로, SQL 템플릿 표현식을 사용하여 쿼리의 특정 부분을 정의할 때 사용된다.
***

### 7. Service단 만들기
```java
package com.springboot.querydsltutorial.service;

import com.springboot.querydsltutorial.dto.UserResponseDto;
import com.springboot.querydsltutorial.entity.User;
import com.springboot.querydsltutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersByName(String name) {
        List<User> userList = userRepository.findUsersByName(name);
        List<UserResponseDto> userResponseDtoList = userList.stream().map(UserResponseDto::of).collect(Collectors.toList());
        return userResponseDtoList;
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersByNameAndEmail(String name, String email) {
        List<User> users = userRepository.findUsersByNameAndEmail(name, email);
        List<UserResponseDto> userResponseDtoList = users.stream().map(UserResponseDto::of).collect(Collectors.toList());
        return userResponseDtoList;
    }
}
```
Service단에서는 그냥 UserRepository와 같이 JpaRepository와 CustomRepository (현재 코드상에서는 UserRepository)를 상속받은 인터페이스를 DI받아서 사용하면 된다.

***
### 8. Controller단에서 데이터 뽑아내기
```java
package com.springboot.querydsltutorial.controller;

import com.springboot.querydsltutorial.dto.UserResponseDto;
import com.springboot.querydsltutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDto> getUsersByName(@RequestParam("name") String name) {
        return userService.findUsersByName(name);
    }

    @GetMapping("/users/search")
    public List<UserResponseDto> getUsersByNameAndEmail(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String email) {
        return userService.findUsersByNameAndEmail(name, email);
    }
    // /users/search : 모든 사용자를 반환한다.
    // /users/search?name=안녕 : 이름이 안녕인 사용자의 데이터를 모두 반환한다.
    // /users/search?email=hello@example.com : 이메일이 hello@example.com인 사용자의 데이터를 모두 반환한다.
    // /users/search?name=안녕&email=hello@example.com : 이름이 "안녕"이고 이메일이 "hello@example.com"인 사용자를 반환한다.
}

```
### 결과. 실제 구현한 코드의 REST API🔫
**/users?name=안녕** : 이름이 안녕인 사용자의 데이터를 모두 반환한다.
![](https://velog.velcdn.com/images/kkong_do/post/df0433a8-9bdd-4b75-8c10-7f9e8dfa6c01/image.png)


#### 동적 쿼리를 사용한 GET Method

** /users/search?name=안녕** : 이름이 안녕인 사용자의 데이터를 모두 반환한다.
![](https://velog.velcdn.com/images/kkong_do/post/aa2c9ee0-2787-4bd6-898a-774f2e6ca88e/image.png)

**/users/search?email=email@naver.com** : 이메일이 email@naver.com인 사용자의 데이터를 모두 반환한다.
![](https://velog.velcdn.com/images/kkong_do/post/1e2c58c9-5156-43a2-b6f4-833cfc282dbb/image.png)
**/users/search?name=안녕&email=email@naver.com** : 이름이 "안녕"이고 이메일이 "email@naver.com"인 사용자를 반환한다.
![](https://velog.velcdn.com/images/kkong_do/post/28d637d7-877d-4992-bdc9-9ed31662a210/image.png)


***
## Querydsl, 그럼 언제 사용할까?🧐
- 가독성 향상, 메서드 네이밍을 통해 쿼리 조건, 정렬 방식을 유추할 수 있다.
- _**고정된 SQL 쿼리를 작성하지 않고 동적으로 쿼리를 생성할 수 있다.**_
- 메서드 분리를 통한 재사용성이 향상된다.
- _**Type-Safe한 Q클래스를 통한 문법으로 인해서 런타임 에러를 방지할 수 있다.**_

>
즉 간단하게 정리하자면 **동적으로 조건문을 주고 싶을때**, **런타임 에러를 방지**하고 싶을때 사용한다.
(Ex. 필터를 걸어서 검색을 하는 경우)

- 하지만 1차 캐시의 장점을 누릴 수 없다. (해당 단점은 JPQL의 특징에도 포함된다.)

***
