package com.example.demo2.user.repository;

import com.example.demo2.user.entity.NineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NineRepository extends JpaRepository<NineEntity, Long> {

    @Modifying
    @Query("delete from NineEntity where docId=?1")
    int deleteByDocId(String docId);

    @Query(nativeQuery = true, value = "select * from nine_tb where docId=?1")
    NineEntity getByDocId(String docId);
}
