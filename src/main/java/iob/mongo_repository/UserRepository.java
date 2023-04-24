package iob.mongo_repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import iob.data.UserEntity;
import iob.data.UserRole;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {

	public List<UserEntity> findAllByVersion(
			@Param("version") int version, 
			Pageable pageable);

	public List<UserEntity> findAllByVersionGreaterThan(
			@Param("minVersion") int minVersion, 
			Pageable pageable);

	public List<UserEntity> findAllByVersionBetween(
			@Param("minVersion") int minVersion,
			@Param("maxVersion") int maxVersion, 
			Pageable pageable);

	public Optional<UserEntity> findByDomainAndEmail(
			@Param("domain") String domain, 
			@Param("email") String email);

	public List<UserEntity> findAllByRole(
			@Param("role") UserRole role);

}
