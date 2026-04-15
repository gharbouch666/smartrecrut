package com.smartrecrute.smartrecrute.dto;

import com.smartrecrute.smartrecrute.enums.NiveauExpertise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilTagRequest {
    private Long tagId;
    private NiveauExpertise niveau;
}