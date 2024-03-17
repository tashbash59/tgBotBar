package Entity;


import javax.persistence.*;

@Entity
@Table(name = "coctails")
public class Coctails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer coctail_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "name", nullable = false)
    private String name;

    public Integer getDes_id() {
        return des_id;
    }

    public void setDes_id(Integer des_id) {
        this.des_id = des_id;
    }

    @Column(name = "des_id", nullable = false)
    private Integer des_id;
}
