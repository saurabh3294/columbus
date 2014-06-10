/**
 * 
 */
package com.proptiger.data.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.stereotype.Component;

/**
 * @author mandeep
 * @author azi
 * 
 */
@Aspect
@Component
public class RepoInterceptor {
    private HTreeMap<Object, Object> hTreeMap;

    public RepoInterceptor() throws IOException {
        DB db = DBMaker.newFileDB(new File("src/test/resources/testdb")).closeOnJvmShutdown().freeSpaceReclaimQ(2)
                .transactionDisable().make();
        hTreeMap = db.getHashMap("hashMap");
    }

    @Around("execution(* com.proptiger.data.repo.*.*(..))")
    private Object interceptRepo(ProceedingJoinPoint pjp) throws IOException, Throwable {
        String key = pjp.getSignature() + ToStringBuilder.reflectionToString(
                pjp.getArgs(),
                ToStringStyle.SHORT_PREFIX_STYLE);

        if (!hTreeMap.containsKey(key)) {
            hTreeMap.putIfAbsent(key, pjp.proceed());
        }
        return hTreeMap.get(key);
    }
}