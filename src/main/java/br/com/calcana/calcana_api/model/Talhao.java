package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Talhao")
@Table(name = "talhao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTalhao")
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTalhao;

    private String identificador;

    @ManyToOne
    @JoinColumn(name = "idZona")
    private Zona zona;
}
