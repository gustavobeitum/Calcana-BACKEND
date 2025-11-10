package br.com.calcana.calcana_api.repositories.specifications;

import br.com.calcana.calcana_api.model.Analises;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class AnaliseSpecification {

    public static Specification<Analises> comDataInicio(LocalDate dataInicio) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dataAnalise"), dataInicio);
    }

    public static Specification<Analises> comDataFim(LocalDate dataFim) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dataAnalise"), dataFim);
    }

    public static Specification<Analises> comTalhao(String talhao) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("talhao"), "%" + talhao + "%");
    }

    public static Specification<Analises> comPropriedadeId(Long propriedadeId) {
        return (root, query, criteriaBuilder) -> {
            Join<Analises, Propriedade> propriedadeJoin = root.join("propriedade");
            return criteriaBuilder.equal(propriedadeJoin.get("idPropriedade"), propriedadeId);
        };
    }

    public static Specification<Analises> comFornecedorId(Long fornecedorId) {
        return (root, query, criteriaBuilder) -> {
            Join<Analises, Propriedade> propriedadeJoin = root.join("propriedade");
            Join<Propriedade, Fornecedor> fornecedorJoin = propriedadeJoin.join("fornecedor");
            return criteriaBuilder.equal(fornecedorJoin.get("idFornecedor"), fornecedorId);
        };
    }
}