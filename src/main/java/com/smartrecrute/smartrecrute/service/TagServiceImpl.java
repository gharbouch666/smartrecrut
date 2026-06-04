package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.TagRepository;
import com.smartrecrute.smartrecrute.repository.TagOffreRepository;
import com.smartrecrute.smartrecrute.repository.ProfilTagRepository;
import com.smartrecrute.smartrecrute.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository repository;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    @Autowired
    private ProfilTagRepository profilTagRepository;

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
        if (existsByLibelle(tag.getLibelle())) {
            throw new IllegalArgumentException("A tag with this name already exists");
        }
        return repository.save(tag);
    }

    @Override
    public Tag update(Long id, Tag tagDetails) {
        Tag tag = getById(id);
        if (existsByLibelleAndIdNot(tagDetails.getLibelle(), id)) {
            throw new IllegalArgumentException("A tag with this name already exists");
        }
        tag.setLibelle(tagDetails.getLibelle());
        tag.setCategorie(tagDetails.getCategorie());
        return repository.save(tag);
    }

    @Override
    public void delete(Long id) {
        if (isTagInUse(id)) {
            long offreCount = tagOffreRepository.countByTagId(id);
            long candidatCount = profilTagRepository.countByTagId(id);
            throw new IllegalStateException(
                "Cannot delete tag: it is used in " + offreCount + " job offer(s) and " + candidatCount + " candidate profile(s)"
            );
        }
        repository.deleteById(id);
    }

    @Override
    public Tag findByLibelle(String libelle) {
        return repository.findByLibelle(libelle);
    }

    @Override
    public boolean isTagInUse(Long id) {
        return tagOffreRepository.existsByTagId(id) || profilTagRepository.existsByTagId(id);
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        return repository.existsByLibelleIgnoreCase(libelle);
    }

    @Override
    public boolean existsByLibelleAndIdNot(String libelle, Long id) {
        return repository.existsByLibelleIgnoreCaseAndIdNot(libelle, id);
    }
}