package com.smartrecrute.smartrecrute.dto;

import com.smartrecrute.smartrecrute.entity.Offre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreWithTagsRequest {
    private Offre offre;
    private List<TagOffreRequest> tags;
}