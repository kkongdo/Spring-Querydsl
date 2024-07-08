package com.springboot.querydsltutorial.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.querydsltutorial.entity.QUser;
import com.springboot.querydsltutorial.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findUsersByName(String name) { // RequestParam으로 받아온 이름을 조건식에 사용해서 해당 조건에 맞는 엔티티 가져오기
        QUser user = QUser.user;
        return queryFactory.selectFrom(user)
                .where(user.name.eq(name))
                .fetch();
    }

    @Override
    public List<User> findUsersByNameAndEmail(String name, String email) {
        QUser user = QUser.user;
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
                .where(builder)
                .fetch();
    }
}
