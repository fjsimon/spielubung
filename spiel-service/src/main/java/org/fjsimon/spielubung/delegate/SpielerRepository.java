package org.fjsimon.spielubung.delegate;

import java.util.Optional;
import org.fjsimon.spielubung.model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpielerRepository extends JpaRepository<Spieler, Long> {

    Spieler save(Spieler spieler);

    Optional<Spieler> findByName(String name);

    boolean existsByName(String name);

    Optional<Spieler> findByNameNotAndGegenspielerIsNull(String name);

    void delete(Spieler spieler);
}