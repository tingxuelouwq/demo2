package com.example.demo2;

import com.example.demo2.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xinhua.cbcloud.core.domain.*;
import org.xinhua.cbcloud.core.enums.RelationType;
import org.xinhua.cbcloud.docs.dt.dto.StoryInfoDTO;
import org.xinhua.cbcloud.docs.dt.service.DtService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class Demo2ApplicationTests {


    @Autowired
    private DtService dtService;

    @Test
    void contextLoads() {
        Document document = new Document();
        document.setDocId("101002021051070000002");
        document.setMediaTypeId("Text");
        document.setHeadline("测试稿件");
        document.setAuthors("王琪,张三");
        document.setWordCount("0");

        List<Publish> publishes = new ArrayList<>();
        Publish publish = new Publish();
        publish.setProductName("新华每日电讯");
        publish.setColumnName("十六版");
        PublishPress press = new PublishPress();
        press.setSchedulePubTime(1620699542753L);
        publish.setPress(press);
        publishes.add(publish);

        document.setPublishs(publishes);

        document.setIssuer("王琪");
        document.setIssueDateTime(1620699542753L);
        document.setUpdateCancelId(1);

        List<EntityRelation> relations = new ArrayList<>();
        EntityRelation relation = new EntityRelation();
        relation.setRelationType(RelationType.CANCEL.getCode());
        List<EntityRelationInfo> infos = new ArrayList<>();
        EntityRelationInfo info = new EntityRelationInfo();
        info.setEntityId("101002021051070000001");
        info.setMediaTypeId("Text");
        infos.add(info);
        relation.setInfos(infos);
        relations.add(relation);

        document.setRelationships(relations);

        StoryInfoDTO storyInfo = dtService.buildStoryInfo(document);
        System.out.println(JsonUtil.bean2Xml(storyInfo));
    }
}
