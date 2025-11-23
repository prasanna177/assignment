package com.payment.entity;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;

import javax.persistence.Table;

@Serdeable
@Introspected
@Table(schema = "operators")
@MappedEntity("merchant")
public class Merchant {

    @Id
    @MappedProperty(value = "id", type = DataType.STRING)
    String id;

    @MappedProperty(value = "name", type = DataType.STRING)
    String name;

    @MappedProperty(value = "email", type = DataType.STRING)
    String email;

    @MappedProperty(value = "phone", type = DataType.STRING)
    String phone;

    @MappedProperty(value = "status", type = DataType.STRING)
    String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
