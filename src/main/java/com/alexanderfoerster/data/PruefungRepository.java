package com.alexanderfoerster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PruefungRepository extends JpaRepository<Pruefung, Long>, JpaSpecificationExecutor<Pruefung> {

}
