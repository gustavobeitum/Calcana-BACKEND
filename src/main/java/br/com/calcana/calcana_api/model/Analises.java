package br.com.calcana.calcana_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // Usaremos BigDecimal para precisão decimal
import java.time.LocalDate; // Para DataAnalise
import java.time.LocalDateTime; // Para Data_Envio_Email

@Entity(name = "Analise") // Nome da entidade
@Table(name = "analises") // Nome da tabela no banco
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
    @JoinColumn(name = "idTalhao")
    private Talhao talhao;

    @ManyToOne
    @JoinColumn(name = "idVariedade")
    private Variedade variedade;

    @ManyToOne
    @JoinColumn(name = "idCorte")
    private Corte corte;

    @ManyToOne
    @JoinColumn(name = "idUsuarioLancamento")
    private Usuario usuarioLancamento;

    private BigDecimal pbu;

    private BigDecimal brix;

    private BigDecimal leituraSacarimetrica;

    private BigDecimal leituraSacarimetricaCorrigida;

    private BigDecimal polCaldo;

    private BigDecimal fibra;

    private BigDecimal pureza;

    private BigDecimal polCana;

    private BigDecimal arCana;

    private BigDecimal atr;

    private BigDecimal adr;

    private Boolean statusEnvioEmail = false;

    private LocalDateTime dataEnvioEmail;
}