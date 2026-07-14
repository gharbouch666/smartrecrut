package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Offre;
import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.dto.TagOffreRequest;
import java.util.List;

public interface OffreService {
    List<Offre> getAll();
    List<Offre> getOpenJobs();
    Offre getById(Long id);
    Offre create(Offre offre);
    Offre update(Long id, Offre offre);
    void delete(Long id);
    Offre createWithTags(Offre offre, List<TagOffreRequest> tags);
    Offre updateWithTags(Long id, Offre offre, List<TagOffreRequest> tags);
    List<TagOffre> getTagsByOffre(Long offreId);
}