package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Propriedade")
@Table(name = "propriedades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPropriedade")
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPropriedade;

    private  String nome;

    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "idFornecedor")
    private Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "idCidade")
    private Cidade cidade;

}
