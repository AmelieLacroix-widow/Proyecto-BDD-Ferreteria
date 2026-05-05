package com.miempresa.ferreteria;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura Jackson para trabajar correctamente con entidades JPA/Hibernate.
 *
 * Sin este módulo, Jackson explota con una de estas excepciones al intentar
 * serializar relaciones FetchType.LAZY que no fueron inicializadas:
 *
 *   - LazyInitializationException  (sesión Hibernate ya cerrada)
 *   - InvalidDefinitionException   (no hay serializador para HibernateProxy)
 *
 * Con Hibernate6Module, los campos lazy NO inicializados se serializan como
 * null en lugar de lanzar excepción — sin importar si open-in-view está activo
 * o no, y sin importar qué entidades tengan @JsonIgnoreProperties o no.
 *
 * REQUISITO en pom.xml:
 *   <dependency>
 *       <groupId>com.fasterxml.jackson.datatype</groupId>
 *       <artifactId>jackson-datatype-hibernate6</artifactId>
 *   </dependency>
 *   (la versión la gestiona el BOM de Spring Boot 3.x, no hace falta declararla)
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer hibernateModuleCustomizer() {
        return builder -> {
            Hibernate6Module module = new Hibernate6Module();

            // Por defecto el módulo serializa los proxies lazy NO inicializados como null.
            // Eso es lo que queremos: Ticket → usuario aparece en el JSON con los
            // campos que SÍ están cargados (idUsuario, nombreUsuario, rol) y sin
            // lanzar excepción por los proxies.
            //
            // NO activamos FORCE_LAZY_LOADING porque haría una consulta SQL extra
            // por cada relación lazy de cada objeto → problema N+1.

            builder.modulesToInstall(module);
        };
    }
}
