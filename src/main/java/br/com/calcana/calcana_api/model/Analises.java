package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "Analise")
@Table(name = "analises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idAnalise")
public class Analises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnalise;

    private Integer numeroAmostra;

    private LocalDate dataAnalise;

    @ManyToOne
    @JoinColumn(name = "idPropriedade")
    private Propriedade propriedade;

    @ManyToOne
    @JoinColumn(name = "idUsuarioLancamento")
    private Usuario usuarioLancamento;

    private String zona;

    private String talhao;

    private Integer corte;

    private BigDecimal pbu;

    private BigDecimal brix;

    private BigDecimal leituraSacarimetrica;

    private BigDecimal leituraSacarimetricaCorrigida;

    private BigDecimal polCaldo;

    private BigDecimal fibra;

    private BigDecimal pureza;

    private BigDecimal polCana;

    private BigDecimal arCana;

    private BigDecimal arCaldo;

    private BigDecimal atr;

    private Boolean statusEnvioEmail = false;

    private LocalDateTime dataEnvioEmail;

    private String observacoes;
}