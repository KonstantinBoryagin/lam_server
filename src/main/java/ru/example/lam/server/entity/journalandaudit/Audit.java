package ru.example.lam.server.entity.journalandaudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

@Data
@Entity
@Table(name = "record")
@DiscriminatorValue("2")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DynamicInsert
@DynamicUpdate
public class Audit extends Record{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_type_id", nullable = false)
    private AuditMessageTypeEntity messageType;

}
