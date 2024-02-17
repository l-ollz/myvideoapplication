package com.myapp.myvideoapplication.repository;

import com.myapp.myvideoapplication.domain.Vlog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Vlog entity.
 */
@Repository
public interface VlogRepository extends JpaRepository<Vlog, Long> {
    @Query("select vlog from Vlog vlog where vlog.user.login = ?#{principal.username}")
    List<Vlog> findByUserIsCurrentUser();

    default Optional<Vlog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Vlog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Vlog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct vlog from Vlog vlog left join fetch vlog.user",
        countQuery = "select count(distinct vlog) from Vlog vlog"
    )
    Page<Vlog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct vlog from Vlog vlog left join fetch vlog.user")
    List<Vlog> findAllWithToOneRelationships();

    @Query("select vlog from Vlog vlog left join fetch vlog.user where vlog.id =:id")
    Optional<Vlog> findOneWithToOneRelationships(@Param("id") Long id);
}
