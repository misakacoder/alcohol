package com.gin.kir;

import com.gin.entity.Alcohol;
import com.gin.vo.AlcoholVO;
import com.kir.annotation.GetMapping;
import com.kir.annotation.RequestMapping;
import com.kir.annotation.RequestParam;
import com.misaka.annotation.Kir;

import java.util.List;

@Kir(url = "gin")
@RequestMapping("/api/v1/alcohol")
public interface Gin {

    @GetMapping("/get")
    AlcoholVO get();

    @GetMapping("/list")
    List<Alcohol> list(@RequestParam("limit") Integer limit);
}
