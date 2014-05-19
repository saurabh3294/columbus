/**
 * 
 */
package com.proptiger.data.util;

import java.io.File;
import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.stereotype.Component;

/**
 * @author mandeep
 *
 */
@Aspect
@Component
public class RepoInterceptor {
    private HTreeMap<Object, Object> hTreeMap;

    public RepoInterceptor() throws IOException {
        DB db = DBMaker.newFileDB(new File("src/test/resources/testdb"))
                .closeOnJvmShutdown()
                .make();
        hTreeMap = db.getHashMap("hashMap");
    }

    @Around("execution(* com.proptiger.data.repo.*.*(..))")
    private Object interceptRepo(ProceedingJoinPoint pjp) throws IOException, Throwable {
        if (hTreeMap.get(pjp.toShortString()) == null) {
            hTreeMap.put(pjp.toShortString(), pjp.proceed());
        }

        return hTreeMap.get(pjp.toShortString());
    }
}
