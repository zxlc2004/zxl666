package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Logistics;
import org.example.springboot.service.LogisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物流管理接口")
@RestController
@RequestMapping("/logistics")
public class LogisticsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogisticsController.class);

    @Autowired
    private LogisticsService logisticsService;

    @Operation(summary = "创建物流信息")
    @PostMapping
    public Result<?> createLogistics(@RequestBody Logistics logistics) {
        return logisticsService.createLogistics(logistics);
    }

    @Operation(summary = "更新物流状态")
    @PutMapping("/{id}/status")
    public Result<?> updateLogisticsStatus(@PathVariable Long id, @RequestParam Integer status) {
        return logisticsService.updateLogisticsStatus(id, status);
    }

    @Operation(summary = "删除物流信息")
    @DeleteMapping("/{id}")
    public Result<?> deleteLogistics(@PathVariable Long id) {
        return logisticsService.deleteLogistics(id);
    }

    @Operation(summary = "根据ID获取物流详情")
    @GetMapping("/{id}")
    public Result<?> getLogisticsById(@PathVariable Long id) {
        return logisticsService.getLogisticsById(id);
    }

    @Operation(summary = "根据订单ID获取物流信息")
    @GetMapping("/order/{orderId}")
    public Result<?> getLogisticsByOrderId(@PathVariable Long orderId) {
        return logisticsService.getLogisticsByOrderId(orderId);
    }

    @Operation(summary = "分页查询物流信息")
    @GetMapping("/page")
    public Result<?> getLogisticsByPage(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        return logisticsService.getLogisticsByPage(orderId, merchantId, status, currentPage, size);
    }

    @Operation(summary = "批量删除物流信息")
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestParam List<Long> ids) {
        return logisticsService.deleteBatch(ids);
    }

    @Operation(summary = "确认签收")
    @PutMapping("/{id}/sign")
    public Result<?> signLogistics(@PathVariable Long id) {
        return logisticsService.signLogistics(id);
    }
} 