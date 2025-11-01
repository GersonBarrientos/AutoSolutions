package com.autosolutions.api;

import com.autosolutions.repo.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClienteRepository clienteRepo;
    private final ServicioRepository servicioRepo;
    private final VehiculoRepository vehiculoRepo;
    private final OrdenTrabajoRepository ordenRepo;
    private final EstadoOrdenRepository estadoRepo;

    @Value("${spring.application.name:AutoSolutions}")
    private String appName;

    @Value("${spring.profiles.active:default}")
    private String profile;

    /**
     * Página principal / Dashboard
     */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        log.info("🏠 Accediendo a la página de inicio");

        // Marcar que el usuario ha visitado Home (para el interceptor)
        session.setAttribute("visitedHome", Boolean.TRUE);

        // Información de la aplicación
        model.addAttribute("appName", appName);
        model.addAttribute("profile", profile);

        try {
            // Métricas del Dashboard
            long clientesCount = clienteRepo.count();
            long serviciosCount = servicioRepo.count();
            long vehiculosCount = vehiculoRepo.count();
            long ordenesCount = ordenRepo.count();
            
            // Órdenes activas (Pendiente + En Proceso)
            long ordenesActivas = ordenRepo.countByEstadoIdIn(java.util.List.of(1, 2));

            model.addAttribute("clientesCount", clientesCount);
            model.addAttribute("serviciosCount", serviciosCount);
            model.addAttribute("vehiculosCount", vehiculosCount);
            model.addAttribute("ordenesCount", ordenesCount);
            model.addAttribute("ordenesActivas", ordenesActivas);

            // Verificar conexión a BD
            long estadosCount = estadoRepo.count();
            model.addAttribute("dbOk", true);
            model.addAttribute("estadosCount", estadosCount);

            log.info("✅ Estadísticas cargadas: {} clientes, {} servicios, {} vehículos, {} órdenes", 
                clientesCount, serviciosCount, vehiculosCount, ordenesCount);

        } catch (Exception e) {
            log.error("❌ Error al cargar estadísticas del dashboard", e);
            model.addAttribute("dbOk", false);
            model.addAttribute("clientesCount", 0);
            model.addAttribute("serviciosCount", 0);
            model.addAttribute("vehiculosCount", 0);
            model.addAttribute("ordenesCount", 0);
            model.addAttribute("ordenesActivas", 0);
        }

        return "index";
    }

    /**
     * Página de prueba para verificar que el CSS se carga correctamente
     */
    @GetMapping("/test-css")
    public String testCss(Model model) {
        model.addAttribute("appName", appName);
        return "index";
    }
}