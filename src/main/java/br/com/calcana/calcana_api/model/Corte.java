package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Corte")
@Table(name = "cortes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idCorte")
public class Corte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorte;

    private String descricao;
}