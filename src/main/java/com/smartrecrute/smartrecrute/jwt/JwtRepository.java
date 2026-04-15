package com.smartrecrute.smartrecrute.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {

    Optional<Jwt> findByValue(String value);

    @Query("SELECT j FROM Jwt j WHERE j.desactivated = :desactivated AND j.expired = :expired AND j.user.email = :email")
    Optional<Jwt> findValidToken(@Param("email") String email,
                                 @Param("expired") boolean expired,
                                 @Param("desactivated") boolean desactivated);

    @Query("SELECT j FROM Jwt j WHERE j.user.email = :email")
    Stream<Jwt> findTokensByUserEmail(@Param("email") String email);

    void deleteAllByExpiredAndDesactivated(boolean expired, boolean desactivated);
    
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM jwt WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}