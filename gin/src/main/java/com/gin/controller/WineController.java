package com.gin.controller;

import com.gin.base.BaseController;
import com.gin.base.BaseList;
import com.gin.base.BasePage;
import com.gin.entity.Wine;
import com.gin.service.WineService;
import com.gin.vo.WineVO;
import com.github.pagehelper.PageInfo;
import com.misaka.annotation.RateLimiter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
@Api(tags = "酒厂")
@Validated
@RestController
@RequestMapping("/api/v1/wine")
public class WineController extends BaseController {

    @Autowired
    private WineService wineService;

    @GetMapping("/get")
    @ApiOperation("获取")
    @RateLimiter(count = 30)
    public WineVO get() {
        return wineService.selectOne(new Wine(), WineVO.class);
    }

    @GetMapping("/list")
    @ApiOperation("列表")
    public List<Wine> list(@RequestParam Integer limit) {
        BaseList baseList = new BaseList();
        baseList.setLimit(limit);
        baseList.setOrderBy("id desc");
        return wineService.list(baseList);
    }

    @GetMapping("/page")
    @ApiOperation("分页")
    public PageInfo<Wine> page(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        BasePage basePage = new BasePage();
        basePage.setPageNum(pageNum);
        basePage.setPageSize(pageSize);
        basePage.setOrderBy("id desc");
        return wineService.page(basePage);
    }
}