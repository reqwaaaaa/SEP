package com.pidu.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean(value = "defaultApi")
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())  // 这里设置所有自定义信息
                .groupName("default")  // 分组名，可改成中文如 "系统接口"
                .select()
                .apis(RequestHandlerSelectors.any())  // 扫描所有 Controller（最宽松，推荐先用这个测试）
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("校企慧公共服务平台 API 文档")  // 修改标题
                .description("校企慧平台后端接口文档，包含用户、岗位、简历等模块")  // 修改简介
                .termsOfServiceUrl("https://your-domain.com/terms")  // 服务条款链接（可改成你的网站）
                .contact(new Contact(
                        "Naiweilanlan",
                        "https://github.com/reqwaaaaa",
                        "2632649293@qq.com"
                ))
                .version("1.0.1")  // 修改版本号
                .build();
    }
}