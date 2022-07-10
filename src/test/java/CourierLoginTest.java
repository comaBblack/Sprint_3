import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class CourierLoginTest {
    public static Response courierLogin(CourierLoginTestData courier){
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        String login = RandomStringUtils.randomAlphabetic(6);
        String password = RandomStringUtils.randomAlphabetic(6);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        CourierCreateTestData courier = new CourierCreateTestData(login, password, firstName);
        CourierCreateTest.createCourier(courier);
    }

    @After

    //курьер может авторизоваться
    //для авторизации нужно передать все обязательные поля
    //успешный запрос возвращает id
    @Test
    @DisplayName("Успешная авторизация курьера")
    public void successCourierLoginTest(){
        //создание курьера
        String login = RandomStringUtils.randomAlphabetic(6);
        String password = RandomStringUtils.randomAlphabetic(6);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        CourierCreateTestData courier = new CourierCreateTestData(login, password, firstName);
        CourierCreateTest.createCourier(courier);

        CourierLoginTestData courierLog = new CourierLoginTestData(login, password);
        courierLogin(courierLog).then().statusCode(200);
        int responseId = courierLogin(courierLog).then().extract().path("id");
        //проверка, что запрос возвращает id
        assertNotNull(responseId);
        //удаление данных о курьере
        given()
                .delete("/api/v1/courier/{id}", responseId);

    }
    //если какого-то поля нет, запрос возвращает ошибку
    @Test
    @DisplayName("Ошибка при авторизации курьера без логина")
    public void courierLoginWithoutLoginErrTest(){
        CourierLoginTestData courier = new CourierLoginTestData("", "1234");
        courierLogin(courier).then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }
    @Test
    @DisplayName("Ошибка при авторизации курьера без пароля")
    public void courierLoginWithoutPassErrTest(){
        CourierLoginTestData courier = new CourierLoginTestData("ninjaKakashi", "");
        courierLogin(courier).then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }
    //система вернёт ошибку, если неправильно указать логин или пароль
    //если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
    @Test
    @DisplayName("Ошибка при авторизации курьера под неверными учетными данными")
    public void courierLoginWithWrongLogpassErrTest(){
        CourierLoginTestData courier = new CourierLoginTestData("ninjaKakashi", "1111");
        courierLogin(courier).then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }
}
