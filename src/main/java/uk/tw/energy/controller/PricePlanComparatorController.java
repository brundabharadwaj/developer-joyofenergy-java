package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);

        return pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId)
                .map(consumptions -> {
                    Map<String, Object> pricePlanComparisons = new HashMap<>();
                    pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
                    pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptions);
                    return ResponseEntity.ok(pricePlanComparisons);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
            @PathVariable String smartMeterId,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId)
                .map(consumptions -> consumptions.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(limit == null ? consumptions.size() : limit)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
