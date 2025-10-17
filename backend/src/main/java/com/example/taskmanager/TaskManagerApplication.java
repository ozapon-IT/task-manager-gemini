package com.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

@SpringBootApplication(scanBasePackages = "com.example.taskmanager")
public class TaskManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerApplication.class, args);
	}

	// ← ★ Hibernateの遅延ロード防止用モジュールを登録
  @Bean
  public Hibernate6Module hibernateModule() {
      Hibernate6Module module = new Hibernate6Module();
      module.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
      module.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
      return module;
  }
}
