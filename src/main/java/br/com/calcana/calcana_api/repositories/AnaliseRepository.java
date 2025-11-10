package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Analises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnaliseRepository extends JpaRepository<Analises, Long>, JpaSpecificationExecutor<Analises> {

    @Query("SELECT AVG(a.atr) FROM Analise a")
    Double findMediaATR();

    @Query("SELECT new br.com.calcana.calcana_api.security.dto.AnaliseMensalDTO(MONTH(a.dataAnalise), COUNT(a), AVG(a.atr)) " +
            "FROM Analise a WHERE YEAR(a.dataAnalise) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(a.dataAnalise) " +
            "ORDER BY MONTH(a.dataAnalise)")
    List<br.com.calcana.calcana_api.security.dto.AnaliseMensalDTO> findAnalisesMensaisDoAno();

    @Query("SELECT COUNT(a) FROM Analise a WHERE YEAR(a.dataAnalise) = YEAR(CURRENT_DATE)")
    Long findContagemAnalisesEsteAno();

    @Query("SELECT MAX(a.dataAnalise) FROM Analise a")
    Optional<LocalDate> findUltimaDataAnalise();

    List<Analises> findTop5ByOrderByDataAnaliseDesc();
}