package technixc.sse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import technixc.sse.dto.redis.RegisteredUserHash;
import technixc.sse.repository.RegisteredUsersRedisRepository;

import java.util.UUID;

/**
 * Service for registering a client via Redis.
 */
@Service
@RequiredArgsConstructor
public class RedisRegistrationServiceImpl implements RegistrationService {

    private final RegisteredUsersRedisRepository usersRepository;

    @Override
    public void registerUser(String userId, String queueId) {
        usersRepository.save(
                RegisteredUserHash.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .queueId(queueId)
                        .build());
    }
}
