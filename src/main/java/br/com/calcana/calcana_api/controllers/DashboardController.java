package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.security.dto.AnaliseMensalDTO;
import br.com.calcana.calcana_api.security.dto.AtividadeRecenteDTO;
import br.com.calcana.calcana_api.security.dto.DashboardMetricsDTO;
import br.com.calcana.calcana_api.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(dashboardService.getMetrics());
    }

    @GetMapping("/analises-mensais")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<List<AnaliseMensalDTO>> getAnalisesMensais() {
        return ResponseEntity.ok(dashboardService.getAnalisesMensais());
    }

    @GetMapping("/atividades-recentes")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<List<AtividadeRecenteDTO>> getAtividadesRecentes() {
        return ResponseEntity.ok(dashboardService.getAtividadesRecentes());
    }
}
