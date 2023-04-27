package ru.example.lam.server.entity.journalandaudit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;

import java.time.LocalDateTime;
import java.util.Set;

import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

@Data
@ToString(exclude = "tags")
@EqualsAndHashCode(exclude = "tags")
@Entity
@Table(name = "record")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER,
                        name = "record_type_id")
@DynamicInsert
@DynamicUpdate
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "header")
    private String header;

    @Column(name = "message")
    private String message;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "event_time", nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventTime;

    @Column(name = "date_time", nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "record")
    @JsonManagedReference
    private Set<Tag> tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "datasource_id", referencedColumnName = "id")
    private Datasource datasource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private Host host;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "method_id", referencedColumnName = "id")
    private Method method;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "namespace_id", referencedColumnName = "id")
    private Namespace namespace;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "system_info_id", referencedColumnName = "id", nullable = false)
    private SystemInfo systemInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_info_id", referencedColumnName = "id")
    private UserInfo userInfo;

}