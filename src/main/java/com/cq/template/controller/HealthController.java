package com.cq.template.controller;

import com.cq.template.common.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康管理器
 *
 * @author cq
 * @since 2024/03/25
 */
@RestController
@RequestMapping("/health")
@Tag(name = "健康管理器")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping("/check")
    @Operation(summary = "健康检查接口")
    public CommonResult<Boolean> check() {
        return CommonResult.success(true);
    }

}
