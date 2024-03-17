package Entity;


import javax.persistence.*;

@Entity
@Table(name = "description")
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer des_id;


    @Column(name = "rec", nullable = false)
    private String rec;

    @Column(name = "history", nullable = false)
    private String history;


    public String getRec() {
        return rec;
    }

    public void setRec(String rec) {
        this.rec = rec;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Integer getDes_id() {
        return des_id;
    }
}
