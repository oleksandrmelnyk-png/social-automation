package com.kayleighrichmond.social_automation.common.mapper;

public interface SimpleMapper<T, E> {

    E mapDtoToEntity(T dto);

}
