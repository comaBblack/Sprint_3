import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class GetListOfOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }
    @Test
    @DisplayName("Список заказов")
    public void getOrderListTest(){
        //создание курьера
        String login = RandomStringUtils.randomAlphabetic(6);
        String password = RandomStringUtils.randomAlphabetic(6);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        CourierCreateTestData courier = new CourierCreateTestData(login, password, firstName);
        CourierCreateTest.createCourier(courier);

        //логин курьера, получение id
        CourierLoginTestData courierLog = new CourierLoginTestData(login, password);
        int courierId = CourierLoginTest.courierLogin(courierLog).then().extract().path("id");

        //создание заказа, получение id
        CreateOrderTest order = new CreateOrderTest("Kakashi", "Hatake", "Konoha, 31 apt.", "Baumanskaya", "+8 880 555 35 35", 4, "2022-06-06", "Saske, come back to Konoha", List.of("BLACK", "GRAY"));
        int orderId = CreateOrderTest.orderCreate(order).then().extract().path("track");

        //принять заказ
        given()
                .header("Content-type", "application/json")
                .when()
                .with().params("id", orderId, "courierId",courierId)
                .put("/api/v1/orders/accept/");

        //проверить список заказов
        Response orderList = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
        orderList.then().assertThat().body("orders", notNullValue());
    }
}
