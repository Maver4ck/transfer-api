package ru.dstreltsov.transferapi.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String name;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false, length = 500)
    private String password;
    @OneToOne(mappedBy = "user")
    private Account account;
    @OneToMany(mappedBy = "user")
    private Set<EmailData> emailData;
    @OneToMany(mappedBy = "user")
    private Set<PhoneData> phoneData;

    public void copy(User other) {
        id = other.getId();
        name = other.getName();
        dateOfBirth = other.getDateOfBirth();
        password = other.getPassword();
        account = other.getAccount();
        emailData = other.getEmailData();
        phoneData = other.getPhoneData();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("dateOfBirth", dateOfBirth)
                .append("password", password)
                .append("emailData", emailData)
                .append("phoneData", phoneData)
                .toString();
    }
}
