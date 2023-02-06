package ${packageName}.${controllerPackageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ${packageName}.${servicePackageName}.${entity}Service;
import ${packageName}.${basePackageName}.BaseController;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
@Validated
@RestController
@RequestMapping("/api/v1/${endpoint}")
public class ${entity}Controller extends BaseController {

    @Autowired
    private ${entity}Service ${lowerEntity}Service;
}