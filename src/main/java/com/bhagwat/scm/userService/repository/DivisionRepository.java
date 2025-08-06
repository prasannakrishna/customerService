package com.bhagwat.scm.userService.repository;

import com.bhagwat.scm.userService.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, String> {}