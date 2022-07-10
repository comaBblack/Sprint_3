import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final List<String> color;

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }
    public static Response orderCreate(CreateOrderTest order){
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                {"Kakashi", "Hatake", "Konoha, 31 apt.", "Baumanskaya", "+8 880 555 35 35", 4, "2022-06-06", "Saske, come back to Konoha", List.of("BLACK", "GRAY")},
                {"Kakashi", "Hatake", "Konoha, 31 apt.", "Baumanskaya", "+8 880 555 35 35", 4, "2022-06-06", "Saske, come back to Konoha", List.of("BLACK")},
                {"Kakashi", "Hatake", "Konoha, 31 apt.", "Baumanskaya", "+8 880 555 35 35", 4, "2022-06-06", "Saske, come back to Konoha", List.of( "GRAY")},
                {"Kakashi", "Hatake", "Konoha, 31 apt.", "Baumanskaya", "+8 880 555 35 35", 4, "2022-06-06", "Saske, come back to Konoha",List.of( "")}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    public void orderCreateTest(){
        CreateOrderTest order = new CreateOrderTest(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        orderCreate(order).then().statusCode(201).and().assertThat().body("track", notNullValue());
    } }

