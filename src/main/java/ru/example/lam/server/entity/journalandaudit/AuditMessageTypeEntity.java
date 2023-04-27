package ru.example.lam.server.entity.journalandaudit;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;

import ru.example.lam.server.dto.audit.enums.AuditMessageType;

@Data
@Entity
@Table(name = "message_type")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditMessageTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false, unique = true)
    private AuditMessageType type;

    @Column(name = "section", nullable = false)
    private String section;

}
