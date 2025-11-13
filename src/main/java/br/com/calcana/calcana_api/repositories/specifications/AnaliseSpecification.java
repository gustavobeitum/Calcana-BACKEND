package br.com.calcana.calcana_api.repositories.specifications;

import br.com.calcana.calcana_api.model.Analises;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.List;

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

    public static Specification<Analises> comPropriedadeIds(List<Long> propriedadeIds) {
        return (root, query, criteriaBuilder) -> {
            if (propriedadeIds == null || propriedadeIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Analises, Propriedade> propriedadeJoin = root.join("propriedade");
            return propriedadeJoin.get("idPropriedade").in(propriedadeIds);
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