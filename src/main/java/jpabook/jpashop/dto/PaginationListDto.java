package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;



@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationListDto<T>{
    private Number count;
    private List<T> data;

}
