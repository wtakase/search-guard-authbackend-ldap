/*
 * Copyright 2016 by floragunn UG (haftungsbeschränkt) - All rights reserved
 * 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed here is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * This software is free of charge for non-commercial and academic use. 
 * For commercial use in a production environment you have to obtain a license 
 * from https://floragunn.com
 * 
 */

package com.floragunn.dlic.auth.ldap.util;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;

import org.elasticsearch.SpecialPermission;
import org.ldaptive.Connection;

public final class Utils {

    private static final String RFC2254_ESCAPE_CHARS = "\\*()\000";
    
    private Utils() {
        
    }
    
    public static void unbindAndCloseSilently(final Connection connection) {
        if (connection == null) {
            return;
        }
        
        final SecurityManager sm = System.getSecurityManager();

        if (sm != null) {
            sm.checkPermission(new SpecialPermission());
        }

        try {
             AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    connection.close();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
         // ignore
        }
    }
    
    /**
     * RFC 2254 string escaping
     */
    public static String escapeStringRfc2254(final String str) {
        
        if(str == null || str.length() == 0) {
            return str;
        }
        
        final StringTokenizer tok = new StringTokenizer(str, RFC2254_ESCAPE_CHARS, true);

        if (tok.countTokens() == 0) {    
            return str;
        }
        
        final StringBuilder out= new StringBuilder();
        while (tok.hasMoreTokens()) {
            final String s = tok.nextToken();
            
            if (s.equals("*")) {
                out.append("\\2a");
            }
            else if (s.equals("(")) {
                out.append("\\28");
            }    
            else if (s.equals(")")) {
                out.append("\\29");
            }    
            else if (s.equals("\\")) {
                out.append("\\5c");
            }
            else if (s.equals("\000")) {
                out.append("\\00");
            }
            else {
                out.append(s);
            }
        }
        return out.toString();
    }    

    public static void printLicenseInfo() {
        System.out.println("*************************************");
        System.out.println("Searchguard LDAP is not free software");
        System.out.println("for commercial use in production.");
        System.out.println("You have to obtain a license if you ");
        System.out.println("use it in production.");
        System.out.println("*************************************");
    }

}
