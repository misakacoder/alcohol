package com.gin.controller;

import cn.hutool.core.util.RandomUtil;
import com.gin.base.BaseController;
import com.gin.base.BaseList;
import com.gin.base.BasePage;
import com.gin.entity.Alcohol;
import com.gin.service.AlcoholService;
import com.gin.vo.AlcoholVO;
import com.github.pagehelper.PageInfo;
import com.misaka.annotation.RateLimiter;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 酒 前端控制器
 * </p>
 *
 * @author sherry
 * @date 2023-02-06
 */
@Validated
@RestController
@RequestMapping("/api/v1/alcohol")
public class AlcoholController extends BaseController {

    @Autowired
    private AlcoholService alcoholService;

    @GetMapping("/get")
    @ApiOperation("获取")
    @RateLimiter(count = 30)
    public AlcoholVO get() {
        return alcoholService.selectOne(new Alcohol(), AlcoholVO.class);
    }

    @GetMapping("/list")
    @ApiOperation("列表")
    public List<Alcohol> list(@RequestParam Integer limit) {
        BaseList baseList = new BaseList();
        baseList.setLimit(limit);
        baseList.setOrderBy("id desc");
        return alcoholService.list(baseList);
    }

    @GetMapping("/page")
    @ApiOperation("分页")
    public PageInfo<Alcohol> page(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        BasePage basePage = new BasePage();
        basePage.setPageNum(pageNum);
        basePage.setPageSize(pageSize);
        basePage.setOrderBy("id desc");
        return alcoholService.page(basePage);
    }

    @PutMapping("")
    @ApiOperation("修改")
    public void update(@RequestBody Alcohol alcohol) {
        alcoholService.updateByPrimaryKeySelective(alcohol);
    }
}