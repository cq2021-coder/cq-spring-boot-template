package com.cq.template.controller;

import cn.hutool.core.io.FileUtil;
import com.cq.template.common.CommonResult;
import com.cq.template.common.ResultCodeEnum;
import com.cq.template.constants.FileConstant;
import com.cq.template.exception.BusinessException;
import com.cq.template.manager.TencentCosManager;
import com.cq.template.mode.entity.User;
import com.cq.template.mode.enums.FileUploadBizEnum;
import com.cq.template.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 文件控制器
 *
 * @author cq
 * @since 2024/06/01
 */
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理接口")
@Slf4j
public class FileController {

    @Resource
    private TencentCosManager tencentCosManager;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public CommonResult<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, String biz) {
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = ThreadLocalUtil.getLoginUser();
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String filepath = String.format("/%s/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), datePath, filename);
        File file = null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            tencentCosManager.putObject(filepath, file);
            // 返回文件可访问地址
            return CommonResult.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile     文件
     * @param fileUploadBizEnum 文件上传 biz enum
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long oneM = 1024 * 1024L;
        switch (fileUploadBizEnum) {
            case USER_AVATAR:
                if (fileSize > oneM) {
                    throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "文件大小不能超过 1M");
                }
                if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                    throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "文件类型错误");
                }
                break;
            default:
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "未知的文件类型");
        }
    }

}
