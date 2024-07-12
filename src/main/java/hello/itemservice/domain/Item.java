package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity //JPA가 사용하는 객체라는 뜻, @Entity가 있어야 JPA가 인식할 수 있음
//@Table(name="Item") //객체명과 테이블명이 같으면 생략 가능
public class Item {

    @Id //pk와 해당 필드를 매핑
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB에서 값을 넣어줌 ex) Autoincrement
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
