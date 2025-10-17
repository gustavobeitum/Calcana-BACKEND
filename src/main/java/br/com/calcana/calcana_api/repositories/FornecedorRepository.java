package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
}