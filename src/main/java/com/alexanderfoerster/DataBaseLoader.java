package com.alexanderfoerster;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.services.PruefungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DataBaseLoader implements CommandLineRunner {
    @Autowired
    private PruefungService pruefungService;

    @Override
    public void run(String... args) throws Exception {
        final var random = new Random();
        final var samplePruefung = new Pruefung(
                LocalDate.now(),
                random.nextInt(10, 100),
                "mock data"
        );
        //pruefungService.update(samplePruefung);
    }
}
