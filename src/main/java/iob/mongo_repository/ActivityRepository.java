package iob.mongo_repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import iob.data.ActivityEntity;
import iob.logic.activities.ActivityId;

public interface ActivityRepository extends MongoRepository<ActivityEntity, ActivityId> {
	public List<ActivityEntity> findAllByVersion(
			@Param("version") int version,
			Pageable pageable);

	public List<ActivityEntity> findAllByVersionGreaterThan(
			@Param("minVersion") int minVersion,
			Pageable pageable);

	public List<ActivityEntity> findAllByVersionBetween(
			@Param("minVersion") int minVersion,
			@Param("maxVersion") int maxVersion,
			Pageable pageable);

	public List<ActivityEntity> findAllByTypeAndInvokedByDomainAndInvokedByEmail(
			@Param("type") String type,
			@Param("invokedByDomain") String domain,
			@Param("invokedByEmail") String email);
}
