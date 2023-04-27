package ru.example.lam.server.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import static ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer.DATE_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDTO implements Serializable {

    private String username;

    @JsonProperty("registrationtime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime registrationTime;

    @JsonProperty(defaultValue = "true")
    private Boolean active;

    private String city;

    @JsonProperty("roles")
    private Set<String> rolesSet;
}
