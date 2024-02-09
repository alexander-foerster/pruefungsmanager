package com.alexanderfoerster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PruefungRepository extends JpaRepository<Pruefung, Long>, JpaSpecificationExecutor<Pruefung> {
    List<Pruefung> findAllByOrderByDatumAsc();
}
