package  com.ap.menabev.service;
import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.DashboardInputDto;


public interface DashboardService {

	ResponseEntity<?> getDashboardKpiData(DashboardInputDto dashboardInputDto);

	ResponseEntity<?> getDashboardKpiChartData(DashboardInputDto dashboardInputDto);

}
