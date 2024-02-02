package com.alexanderfoerster.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Long> {
    List<Teilnehmer> findAllByPruefungOrderByNachname(Pruefung pruefung);
}