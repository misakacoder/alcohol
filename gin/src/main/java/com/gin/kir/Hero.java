package com.gin.kir;

import com.kir.annotation.GetMapping;
import com.kir.annotation.HttpHeader;
import com.kir.annotation.HttpHeaders;
import com.kir.annotation.PathVariable;
import com.misaka.annotation.Kir;

@Kir(value = "${kir.hero.url}", timeout = 3L)
@HttpHeaders(@HttpHeader(name = "User-Agent", value = "Kir"))
public interface Hero {

    @GetMapping(value = "/images/lol/act/img/js/hero/{id}.js", timeout = 3L)
    String search(@PathVariable("id") String id);
}
