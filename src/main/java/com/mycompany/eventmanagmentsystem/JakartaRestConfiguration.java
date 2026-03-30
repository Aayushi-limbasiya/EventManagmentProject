

package com.mycompany.eventmanagmentsystem;

import Rest.AuthManagmentREST;
import Rest.EventManagmentREST;
import Rest.UserManagementREST;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class JakartaRestConfiguration extends Application {
       @Override
        public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(UserManagementREST.class);
        classes.add(AuthManagmentREST.class);  // ← ADD THIS
        classes.add(EventManagmentREST.class);  // ← ADD THIS

        return classes;
    }
}


