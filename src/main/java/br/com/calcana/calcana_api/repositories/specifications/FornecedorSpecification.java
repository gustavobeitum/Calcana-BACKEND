package br.com.calcana.calcana_api.repositories.specifications;

import br.com.calcana.calcana_api.model.Fornecedor;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class FornecedorSpecification {

    public static Specification<Fornecedor> comTermoDeBusca(String termo) {
        return (root, query, criteriaBuilder) -> {

            Predicate likeNome = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")),
                    "%" + termo.toLowerCase() + "%"
            );

            Predicate likeEmail = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + termo.toLowerCase() + "%"
            );

            return criteriaBuilder.or(likeNome, likeEmail);
        };
    }

    public static Specification<Fornecedor> comStatus(Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("ativo"), ativo);
        };
    }
}