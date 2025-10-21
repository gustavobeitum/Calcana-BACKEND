package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Perfil")
@Table(name = "perfis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPerfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPerfil;

    private String descricao;
}