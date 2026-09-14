package com.youthx.backend.repository;


import com.youthx.backend.entity.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {

    List<PostPhoto> findByPostIdOrderByDisplayOrderAsc(Long postId);
}
