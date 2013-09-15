package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.image.Image;

@Repository
public interface ImageDao extends JpaRepository<Image, Integer> {
}
