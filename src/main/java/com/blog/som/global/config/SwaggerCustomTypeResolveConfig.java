package com.blog.som.global.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;
import springfox.documentation.schema.DefaultTypeNameProvider;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;

@Configuration
public class SwaggerCustomTypeResolveConfig {

  @Bean
  public PluginRegistry<TypeNameProviderPlugin, DocumentationType> customTypeNameResolvers(){
    return PluginRegistry.of(new DefaultTypeNameProvider(){
      @Override
      public String nameFor(Class<?> type) {
        return type.getName();
      }
    });
  }

  @Bean
  @Primary
  public TypeNameExtractor customTypeNameExtractor(TypeResolver typeResolver,
      @Qualifier("customTypeNameResolvers") PluginRegistry<TypeNameProviderPlugin, DocumentationType> customTypeNameResolvers,
      EnumTypeDeterminer determiner){
    return new TypeNameExtractor(typeResolver, customTypeNameResolvers, determiner);
  }
}