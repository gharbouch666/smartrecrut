package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.tag.TagOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagOffreRepository extends JpaRepository<TagOffre, Long> {
}