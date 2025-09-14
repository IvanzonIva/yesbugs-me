package requests.skelethon.requests;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEndpointinterface;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointinterface {
    private CrudRequesters crudRequesters;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequesters = new CrudRequesters(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequesters.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequesters.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel model) {
        return (T) crudRequesters.update(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
