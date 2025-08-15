// src/main/java/com/nexxserve/medadmin/security/HasRole.java
package com.nexxserve.medadmin.security;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasRole {
    String[] value();
}