package Entity;


import javax.persistence.*;

@Entity
@Table(name = "description")
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer des_id;


    @Column(name = "recipe", nullable = false)
    private String recipe;

    @Column(name = "history", nullable = false)
    private String history;





}
