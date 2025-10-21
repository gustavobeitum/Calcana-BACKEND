package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Usuario")
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nome;

    private String email;

    private String senha;

    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "idPerfil")
    private Perfil perfil;
}