package br.com.calcana.calcana_api.repositories.specifications;

import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PropriedadeSpecification {

    public static Specification<Propriedade> comStatus(Boolean ativo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("ativo"), ativo);
    }

    public static Specification<Propriedade> comTermoDeBusca(String termo) {
        return (root, query, criteriaBuilder) -> {

            Join<Propriedade, Fornecedor> fornecedorJoin = root.join("fornecedor");
            Join<Propriedade, Cidade> cidadeJoin = root.join("cidade");

            String termoLower = "%" + termo.toLowerCase() + "%";

            Predicate likeNomePropriedade = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")), termoLower
            );

            Predicate likeNomeFornecedor = criteriaBuilder.like(
                    criteriaBuilder.lower(fornecedorJoin.get("nome")), termoLower
            );

            Predicate likeNomeCidade = criteriaBuilder.like(
                    criteriaBuilder.lower(cidadeJoin.get("nome")), termoLower
            );

            return criteriaBuilder.or(likeNomePropriedade, likeNomeFornecedor, likeNomeCidade);
        };
    }
}