package com.alexanderfoerster.services;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.data.PruefungRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PruefungService {
    private final PruefungRepository repository;

    public PruefungService(PruefungRepository repository) {
        this.repository = repository;
    }

    public Optional<Pruefung> get(Long id) {
        return repository.findById(id);
    }

    public Pruefung update(Pruefung entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
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
}
