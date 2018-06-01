package org.fage.mysearchengine.web.repository;

import org.fage.mysearchengine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午4:58 2018/5/4
 * @description
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query("select u.id from User u where u.username = :username")
    Integer findIdByUsername(@Param("username") String username);
}
