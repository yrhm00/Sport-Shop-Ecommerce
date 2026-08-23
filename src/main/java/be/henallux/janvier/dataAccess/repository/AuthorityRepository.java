package be.henallux.janvier.dataAccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.AuthorityEntity;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Integer> {

    boolean existsByUsernameAndAuthority(String username, String authority);
}

