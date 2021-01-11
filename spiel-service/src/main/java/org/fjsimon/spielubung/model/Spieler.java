package org.fjsimon.spielubung.model;

import lombok.*;
import javax.persistence.*;

@Data
@Setter
@Getter
@Entity(name = "spieler")
@ToString(exclude = "gegenspieler")
@NoArgsConstructor
public class Spieler {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column(name = "isavailable")
    private boolean available;

    @Column(name = "isprimary")
    private boolean primary;

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="gegenspieler_id")
    private Spieler gegenspieler;

    public Spieler(String name) {
        this.name = name;
        this.available = true;
    }
}