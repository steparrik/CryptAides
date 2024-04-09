package steparrik.code.crypraidesonwebhooks.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "coin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String name;

    double count;

    double buyPrice;

    double currentPrice;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Coin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", buyPrice=" + buyPrice +
                ", currentPrice=" + currentPrice +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return Objects.equals(name, coin.name) && Objects.equals(user, coin.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }
}
