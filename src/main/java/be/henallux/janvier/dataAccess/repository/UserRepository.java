package be.henallux.janvier.dataAccess.repository;

import be.henallux.janvier.dataAccess.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    // Méthode personnalisée pour trouver un utilisateur par username
    UserEntity findByUsername(String username);
    
    // Méthode personnalisée pour trouver un utilisateur par email
    UserEntity findByEmail(String email);
    
    // Méthode pour vérifier si un username existe déjà
    boolean existsByUsername(String username);
    
    // Méthode pour vérifier si un email existe déjà
    boolean existsByEmail(String email);
}


