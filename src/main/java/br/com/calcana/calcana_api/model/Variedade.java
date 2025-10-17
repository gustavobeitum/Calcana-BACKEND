package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Variedade")
@Table(name = "variedades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idVariedade")
public class Variedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVariedade;

    private String nome;

    private String descricao;
}