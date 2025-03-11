package uk.tw.energy.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.MeterReadingService;
@WebMvcTest(MeterReadingsController.class)
@ExtendWith(MockitoExtension.class)
public class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeterReadingService meterReadingService;

    private static final String SMART_METER_ID = "10101010";

    @Test
    public void givenValidMeterIdShouldReturnReadings() throws Exception {
        List<ElectricityReading> electricityReadings = List.of(
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(10.5)),
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(15.2))
        );
        when(meterReadingService.getReadings(SMART_METER_ID)).thenReturn(Optional.of(electricityReadings));

        // Act & Assert
        mockMvc.perform(get("/readings/read/{smartMeterId}", SMART_METER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reading").value(10.5))
                .andExpect(jsonPath("$[1].reading").value(15.2));

        verify(meterReadingService, times(1)).getReadings(SMART_METER_ID);
    }
    @Test
    public void givenValidMeterIdWhenNoDataFoundShouldReturnNotFound() throws Exception {
        when(meterReadingService.getReadings(SMART_METER_ID)).thenReturn(Optional.of(Collections.emptyList()));
        mockMvc.perform(get("/readings/read/{smartMeterId}", SMART_METER_ID))
                .andExpect(status().isNotFound());
    }




}
