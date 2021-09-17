package com.example.demo2.user.repository;

import com.example.demo2.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * kevin<br/>
 * 2020/11/5 18:31<br/>
 */
public interface UserRepository extends JpaRepository<User, Integer > {

    Optional<User> getById(Integer id);

    List<User> findAllByAgeAfter(Integer age);

    @Query(value = "select count(distinct id) from user where uname = :uname", nativeQuery = true)
    int countById(String uname);
}
