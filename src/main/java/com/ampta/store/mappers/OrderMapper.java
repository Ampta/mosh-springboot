package com.ampta.store.mappers;

import com.ampta.store.dtos.OrderDto;
import com.ampta.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
