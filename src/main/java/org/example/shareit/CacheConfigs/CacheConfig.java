package org.example.shareit.CacheConfigs;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJacksonJsonRedisSerializer redisSerializer=GenericJacksonJsonRedisSerializer
                .create(builder->{});

        GenericJacksonJsonRedisSerializer redisSerializer1=GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();

        RedisCacheWriter redisCacheWriter=RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        RedisCacheConfiguration defaultConfig=RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer1))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configMap = new HashMap<String, RedisCacheConfiguration>();

        configMap.put("items", defaultConfig.entryTtl(Duration.ofMinutes(5)));


        configMap.put("users", defaultConfig.entryTtl(Duration.ofMinutes(15)));

        configMap.put("feedbacks", defaultConfig.entryTtl(Duration.ofMinutes(20)));

        return RedisCacheManager.builder()
                .cacheDefaults(defaultConfig)
                .enableStatistics()
                .withInitialCacheConfigurations(configMap)
                .cacheWriter(redisCacheWriter)
                .build();
    }
}
