package com.example.myvocab.mapper;

import com.example.myvocab.dto.UserTopicVocabDto;
import com.example.myvocab.model.UserTopicVocab;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-08-27T22:55:31+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.3.1 (Oracle Corporation)"
)
@Component
public class UserTopicVocabMapperImpl implements UserTopicVocabMapper {

    @Override
    public void updateUserTopicVocabFromDto(UserTopicVocabDto dto, UserTopicVocab entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        entity.setStatus( dto.isStatus() );
        if ( dto.getUserTopic() != null ) {
            entity.setUserTopic( dto.getUserTopic() );
        }
        if ( dto.getVocab() != null ) {
            entity.setVocab( dto.getVocab() );
        }
        entity.setLearn( dto.isLearn() );
    }
}
