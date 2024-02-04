package com.alexanderfoerster.services;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.data.PruefungRepository;
import com.alexanderfoerster.data.TeilnehmerRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PruefungService {
    private final PruefungRepository repository;
    private final TeilnehmerRepository teilnehmerRepository;

    public PruefungService(PruefungRepository repository, TeilnehmerRepository teilnehmerRepository) {
        this.repository = repository;
        this.teilnehmerRepository = teilnehmerRepository;
    }

    public Optional<Pruefung> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Optional<Pruefung> getWithTeilnehmers(Long pruefungId) {
        Optional<Pruefung> pruefung = repository.findById(pruefungId);
        if (pruefung.isPresent()) {
            Hibernate.initialize(pruefung.get().getTeilnehmers());
        }
        return pruefung;
    }

    public Pruefung update(Pruefung entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        teilnehmerRepository.deleteAllByPruefungId(id);
        repository.deleteById(id);
    }

    public Page<Pruefung> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Pruefung> list(Pageable pageable, Specification<Pruefung> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Pruefung> listAll() {
        return repository.findAll();
    }

    @Transactional
    public int getAnzTeilnehmer(Optional<Pruefung> pruefungFromBackend) {
        if (pruefungFromBackend.isPresent()) {
            var pruefung = pruefungFromBackend.get();
            Hibernate.initialize(pruefung.getTeilnehmers());
            return pruefung.getTeilnehmers().size();
        }
        return 0;
    }
}
