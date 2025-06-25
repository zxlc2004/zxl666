package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springboot.common.Result;
import org.example.springboot.entity.StockIn;
import org.example.springboot.entity.StockOut;
import org.example.springboot.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存管理接口")
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @Operation(summary = "创建入库记录")
    @PostMapping("/in")
    public Result<?> createStockIn(@RequestBody StockIn stockIn) {
        return stockService.createStockIn(stockIn);
    }

    @Operation(summary = "创建出库记录")
    @PostMapping("/out")
    public Result<?> createStockOut(@RequestBody StockOut stockOut) {
        return stockService.createStockOut(stockOut);
    }

    @Operation(summary = "获取入库记录列表")
    @GetMapping("/in/list")
    public Result<?> getStockInList(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(stockService.getStockInList(productId, supplier, status, operatorId, currentPage, size));
    }

    @Operation(summary = "获取出库记录列表")
    @GetMapping("/out/list")
    public Result<?> getStockOutList(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String orderNo,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(stockService.getStockOutList(productId, type, status, operatorId, customerName, orderNo, currentPage, size));
    }

    @Operation(summary = "作废入库记录")
    @PutMapping("/in/{id}/invalidate")
    public Result<?> invalidateStockIn(@PathVariable Long id) {
        return stockService.invalidateStockIn(id);
    }

    @Operation(summary = "作废出库记录")
    @PutMapping("/out/{id}/invalidate")
    public Result<?> invalidateStockOut(@PathVariable Long id) {
        return stockService.invalidateStockOut(id);
    }
} 