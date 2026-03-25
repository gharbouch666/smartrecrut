package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.tag.Tag;
import java.util.List;

public interface TagService {
    List<Tag> getAll();
    Tag getById(Long id);
    Tag create(Tag tag);
    Tag update(Long id, Tag tag);
    void delete(Long id);
    Tag findByLibelle(String libelle);
}