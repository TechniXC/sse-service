package technixc.sse.repository;

import org.springframework.data.repository.CrudRepository;
import technixc.sse.dto.redis.RegisteredUserHash;

import java.util.List;
import java.util.Optional;

public interface RegisteredUsersRedisRepository extends CrudRepository<RegisteredUserHash, String> {

    Optional<RegisteredUserHash> findByQueueId(String queueId);
    List<RegisteredUserHash> findByUserId(String userId);
}
