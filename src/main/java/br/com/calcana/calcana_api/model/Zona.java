package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Zona")
@Table(name = "zonas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idZona")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZona;

    private String nome;

    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "idPropriedade")
    private Propriedade propriedade;

}
