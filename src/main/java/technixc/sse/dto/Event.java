package technixc.sse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
@Reflective
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event implements Serializable {

    private String userId;
    private String eventType;
    private String message;
    private Map<String, Object> data;

}
