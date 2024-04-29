package fr.perso.springserie.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected @NotNull String getDatabaseName() {
        return "series_list";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
