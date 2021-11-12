package com.example.demo2.user.service.impl;

import com.example.demo2.user.entity.NineEntity;
import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.NineRepository;
import com.example.demo2.user.repository.UserRepository;
import com.example.demo2.user.service.HibernateService;
import com.example.demo2.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HibernateServiceImpl implements HibernateService {

    @Autowired
    private NineRepository nineRepository;

    public void add(NineEntity nineEntity) {
        nineRepository.save(nineEntity);
    }

    private List<NineEntity> setNewList(List<NineEntity> list) {
        String remarks = UUID.randomUUID().toString();
        log.info("remarks: {}", remarks);
        return list.stream().map(nineEntity -> {
            NineEntity newEntity = new NineEntity();
            newEntity.setDocId("101001");
            newEntity.setFirstName(nineEntity.getFirstName());
            newEntity.setLastName(nineEntity.getLastName());
            newEntity.setRemarks(remarks);
            return newEntity;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void update() {
        List<NineEntity> list = nineRepository.findAll();

        List<NineEntity> newList = setNewList(list);

        list.forEach(entity -> {
            nineRepository.deleteByDocId(entity.getDocId());
        });

//        nineRepository.deleteAll(list);
        nineRepository.saveAll(newList);

//        nineRepository.deleteByDocId("101001");
//
//        NineEntity nineEntity = new NineEntity();
//        nineEntity.setId(1L);
//        nineEntity.setDocId("101001");
//        nineEntity.setFirstName("first");
//        nineEntity.setLastName("last");
//        nineRepository.save(nineEntity);
//
//        System.out.println(nineRepository.getByDocId("101001"));
    }


    @Transactional
    public void update1() {
        List<NineEntity> list = nineRepository.findAll();

        List<NineEntity> newList = setNewList(list);
        nineRepository.deleteAll(list);

        nineRepository.flush();

        nineRepository.saveAll(newList);
    }
}
