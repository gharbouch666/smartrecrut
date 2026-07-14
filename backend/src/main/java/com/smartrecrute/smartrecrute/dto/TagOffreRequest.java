package com.smartrecrute.smartrecrute.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagOffreRequest {
    private Long tagId;
    private Boolean obligatoire;
    private Double poids;
}