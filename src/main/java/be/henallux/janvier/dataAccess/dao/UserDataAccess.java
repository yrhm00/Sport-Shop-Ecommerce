package be.henallux.janvier.dataAccess.dao;

import be.henallux.janvier.model.User;


public interface UserDataAccess {
    
    User save(User user);
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}


