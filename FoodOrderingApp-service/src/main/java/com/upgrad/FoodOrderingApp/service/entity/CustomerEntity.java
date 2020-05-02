package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Entity
@Table(name = "customer")
@NamedQueries({
        @NamedQuery(name = "getCustomerByContactNumber", query = "select u from CustomerEntity u where  u.contact_number = :contact_number"),
        @NamedQuery(name = "getCustomerByUUID", query = "select u from CustomerEntity u where  u.uuid = :uuid"),
        @NamedQuery(name = "getCustomerByEmail", query = "select ce from CustomerEntity ce where ce.email_address =:email")


})

public class CustomerEntity  implements  Serializable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uuid")
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    @Size(max = 30)
    private String first_name;

    @Column(name = "lastname")
  //  @NotNull
    @Size(max = 30)
    private String last_name;

    @Column(name = "email")
    @NotNull
    @Size(max = 50)
    private String email_address;

    @Column(name = "password")
    @NotNull
    @Size(max = 255)
    private String password;

    @Column(name = "contact_number")
    @Size(max = 30)
    private String contact_number;

    @Column(name = "salt")
    @NotNull
    @Size(max = 255)
    private String salt;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return first_name;
    }
    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }
    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }
    public String getEmail() {
        return email_address;
    }

    public void setEmail(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getContactNumber() {
        return contact_number;
    }

    public void setContactNumber(String contactNumber) {
        this.contact_number = contactNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);//
//    }

}
