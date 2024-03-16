package Entity;


import javax.persistence.*;

@Entity
@Table(name = "coctails")
public class Coctails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer coctail_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "des_id", nullable = false)
    private String des_id;
}
