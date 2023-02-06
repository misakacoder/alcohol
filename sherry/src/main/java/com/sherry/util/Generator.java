package com.sherry.util;

import com.sherry.Sherry;
import com.sherry.config.GeneratorConfig;
import com.sherry.consts.GeneratorConsts;
import com.sherry.converter.DatabaseParser;
import com.sherry.enums.Database;
import com.sherry.enums.FileType;
import com.sherry.enums.ORM;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {

    private static final Logger log = LoggerFactory.getLogger(Generator.class);
    private static final Configuration CONFIGURATION = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    private final Map<String, Object> data = new HashMap<>();
    private final GeneratorConfig config;

    public Generator(GeneratorConfig config) {
        this.config = config;
    }

    public void generate(ORM orm) {
        CONFIGURATION.setClassLoaderForTemplateLoading(Sherry.class.getClassLoader(), "templates");
        String url = config.getUrl();
        String packageName = config.getPackageName();
        String databaseName = config.getDatabaseName();
        String tableName = config.getTableName();
        String entity = StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(tableName));
        Database database = DBUtil.getDatabaseByUrl(url);
        DatabaseParser parser = DatabaseParser.getParser(database);
        List<String> enumList = parser.enums();
        data.put("orm", orm);
        data.put("database", database);
        data.put("packageName", packageName);
        data.put("author", config.getAuthor());
        data.put("date", new SimpleDateFormat(GeneratorConsts.DATE_FORMAT).format(new Date()));
        data.put("entityPackageName", GeneratorConsts.ENTITY_PACKAGE_NAME);
        data.put("basePackageName", GeneratorConsts.BASE_PACKAGE_NAME);
        data.put("handlerPackageName", GeneratorConsts.HANDLER_PACKAGE_NAME);
        data.put("enumPackageName", GeneratorConsts.ENUM_PACKAGE_NAME);
        data.put("mapperPackageName", GeneratorConsts.MAPPER_PACKAGE_NAME);
        data.put("servicePackageName", GeneratorConsts.SERVICE_PACKAGE_NAME);
        data.put("serviceImplPackageName", GeneratorConsts.SERVICE_IMPL_PACKAGE_NAME);
        data.put("controllerPackageName", GeneratorConsts.CONTROLLER_PACKAGE_NAME);
        data.put("configPackageName", GeneratorConsts.CONFIG_PACKAGE_NAME);
        data.put("factoryPackageName", GeneratorConsts.FACTORY_PACKAGE_NAME);
        data.put("interceptorPackageName", GeneratorConsts.INTERCEPTOR_PACKAGE_NAME);
        data.put("entity", entity);
        data.put("enums", enumList);
        data.put("lowerEntity", StringUtil.toLowerCaseFirstOne(StringUtil.lineToHump(tableName)));
        data.put("endpoint", tableName.toLowerCase().replace("_", "-"));
        try (
                JDBC jdbc = new JDBC.Builder()
                        .url(url)
                        .driver(config.getDriver())
                        .username(config.getUsername())
                        .password(config.getPassword())
                        .build()
        ) {
            data.put("table", parser.table(jdbc, databaseName, tableName));
            data.put("columns", parser.columns(jdbc, databaseName, tableName));
            generate("entity.ftl", FileType.JAVA, GeneratorConsts.ENTITY_PACKAGE_NAME, entity);
            generate("baseEnum.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseEnum");
            generate("mapper.ftl", FileType.JAVA, GeneratorConsts.MAPPER_PACKAGE_NAME, entity + "Mapper");
            generate("mapper.xml.ftl", FileType.XML, GeneratorConsts.MAPPER_PACKAGE_NAME, entity + "Mapper");
            generate("service.ftl", FileType.JAVA, GeneratorConsts.SERVICE_PACKAGE_NAME, entity + "Service");
            generate("serviceImpl.ftl", FileType.JAVA, GeneratorConsts.SERVICE_IMPL_PACKAGE_NAME, entity + "ServiceImpl");
            generate("baseController.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseController");
            generate("controller.ftl", FileType.JAVA, GeneratorConsts.CONTROLLER_PACKAGE_NAME, entity + "Controller");
            generate("baseEnumConverterFactory.ftl", FileType.JAVA, GeneratorConsts.FACTORY_PACKAGE_NAME, "BaseEnumConverterFactory");
            generate("webMvcConfig.ftl", FileType.JAVA, GeneratorConsts.CONFIG_PACKAGE_NAME, "WebMvcConfig");
            generate("mybatisConfig.ftl", FileType.JAVA, GeneratorConsts.CONFIG_PACKAGE_NAME, "MybatisConfig");
            generate("myBatisQueryInterceptor.ftl", FileType.JAVA, GeneratorConsts.INTERCEPTOR_PACKAGE_NAME, "MyBatisQueryInterceptor");
            if (orm == ORM.MYBATIS) {
                generate("baseList.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseList");
                generate("basePage.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BasePage");
                generate("baseMapper.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseMapper");
                generate("baseService.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseService");
                generate("baseServiceImpl.ftl", FileType.JAVA, GeneratorConsts.BASE_PACKAGE_NAME, "BaseServiceImpl");
                generate("baseEnumTypeHandler.ftl", FileType.JAVA, GeneratorConsts.HANDLER_PACKAGE_NAME, "BaseEnumTypeHandler");
            }
            if (orm == ORM.TK_MYBATIS) {
                generate("baseEnumTypeHandler.ftl", FileType.JAVA, GeneratorConsts.HANDLER_PACKAGE_NAME, "BaseEnumTypeHandler");
                generate("example.ftl", FileType.JAVA, GeneratorConsts.ENTITY_PACKAGE_NAME, entity + "Example");
            }
            if (!enumList.isEmpty()) {
                for (String enumName : enumList) {
                    data.put("enum", enumName);
                    generate("enum.ftl", FileType.JAVA, GeneratorConsts.ENUM_PACKAGE_NAME, enumName);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void generate(String templateName, FileType fileType, String childPackageName, String className) throws Exception {
        String filename = String.format(fileType == FileType.JAVA ? GeneratorConsts.JAVA_FILE_NAME : GeneratorConsts.XML_FILE_NAME, config.getModule(), StringUtil.packageNameToPath(config.getPackageName()), StringUtil.packageNameToPath(childPackageName), className);
        Template template = CONFIGURATION.getTemplate(templateName, StandardCharsets.UTF_8.toString());
        File outputFile = new File(filename);
        String path = outputFile.getAbsolutePath();
        if (outputFile.exists()) {
            log.info("Exist：{}", path);
            return;
        }
        outputFile.getParentFile().mkdirs();
        log.info("New：{}", path);
        try (Writer writer = new FileWriter(filename)) {
            template.process(data, writer);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
