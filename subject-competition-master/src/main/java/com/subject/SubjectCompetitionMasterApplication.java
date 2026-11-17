package com.subject;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@MapperScan("com.subject.mapper")
@ServletComponentScan(basePackages = "com.subject")
@SpringBootApplication
@Slf4j
public class SubjectCompetitionMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubjectCompetitionMasterApplication.class, args);
        log.info("项目启动成功!");
    }
}
