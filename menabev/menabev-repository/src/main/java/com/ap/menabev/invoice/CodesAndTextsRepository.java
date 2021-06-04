package com.ap.menabev.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.CodesAndTextsDo;

@Repository
public interface CodesAndTextsRepository extends JpaRepository<CodesAndTextsDo, String> {

}
