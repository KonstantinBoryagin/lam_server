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

@Data
@Entity
@Table(name = "datasource")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Datasource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "datasource", nullable = false)
    private String datasourceValue;

}