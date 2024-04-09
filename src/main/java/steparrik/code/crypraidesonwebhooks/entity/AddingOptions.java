package steparrik.code.crypraidesonwebhooks.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "adding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddingOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String adding;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "AddingOptions{" +
                "adding='" + adding + '\'' +
                ", user=" + user +
                '}';
    }
}

