package technixc.sse.dto.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash("sse-push-user")
public class RegisteredUserHash {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String queueId;
}
