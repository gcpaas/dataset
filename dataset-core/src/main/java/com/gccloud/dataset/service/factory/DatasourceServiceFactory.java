package com.gccloud.dataset.service.factory;

import com.gccloud.dataset.service.IBaseDatasourceService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:42
 */
@Service
public class DatasourceServiceFactory {

    @Resource
    private ApplicationContext applicationContext;

    public IBaseDatasourceService build(String type) {
        return applicationContext.getBean(type.toLowerCase(Locale.ROOT), IBaseDatasourceService.class);
    }

}
