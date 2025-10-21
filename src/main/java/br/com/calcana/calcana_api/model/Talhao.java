package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Talhao")
@Table(name = "talhoes")
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

    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "idZona")
    private Zona zona;
}
