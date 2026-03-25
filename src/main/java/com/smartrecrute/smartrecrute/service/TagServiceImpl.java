package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.TagRepository;
import com.smartrecrute.smartrecrute.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository repository;

    @Override
    public List<Tag> getAll() {
        return repository.findAll();
    }

    @Override
    public Tag getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    @Override
    public Tag create(Tag tag) {
        return repository.save(tag);
    }

    @Override
    public Tag update(Long id, Tag tagDetails) {
        Tag tag = getById(id);
        tag.setLibelle(tagDetails.getLibelle());
        tag.setCategorie(tagDetails.getCategorie());
        return repository.save(tag);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Tag findByLibelle(String libelle) {
        return repository.findByLibelle(libelle);
    }
}