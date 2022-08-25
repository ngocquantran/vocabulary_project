package com.example.myvocab.mapper;


import com.example.myvocab.dto.UserTopicVocabDto;
import com.example.myvocab.model.UserTopicVocab;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserTopicVocabMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserTopicVocabFromDto(UserTopicVocabDto dto, @MappingTarget UserTopicVocab entity);

}